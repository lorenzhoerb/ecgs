package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantSelfRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlagsResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCredentialUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantSelfRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.Flags;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenListException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.FlagsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import at.ac.tuwien.sepm.groupphase.backend.util.PasswordGenerator;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.ClubManagerTeamImportDtoValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.ForgotPasswordValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.ImportManyFlagsValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.PasswordChangeValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.RegistrationValidator;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationUserRepository userRepository;
    private final SecurityUserRepository securityUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RegistrationValidator registrationValidator;
    private final PasswordChangeValidator passwordChangeValidator;
    private final ForgotPasswordValidator forgotPasswordValidator;
    private final UserMapper userMapper;

    private final EmailService emailService;

    private final ManagedByRepository managedByRepository;
    private final ClubManagerTeamImportDtoValidator teamValidator;

    private final CompetitionRegistrationService competitionRegistrationService;
    private final SessionUtils sessionUtils;
    private final ImportManyFlagsValidator flagsValidator;
    private final FlagsRepository flagsRepository;

    private final CompetitionRepository competitionRepository;


    @Autowired
    public CustomUserDetailService(ApplicationUserRepository userRepository,
                                   SecurityUserRepository securityUserRepository, PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer, ManagedByRepository managedByRepository,
                                   ClubManagerTeamImportDtoValidator teamValidator,
                                   EmailService emailService, UserMapper userMapper, SessionUtils sessionUtils,
                                   RegistrationValidator registrationValidator,
                                   PasswordChangeValidator passwordChangeValidator, ForgotPasswordValidator forgotPasswordValidator,
                                   CompetitionRegistrationService competitionRegistrationService, ImportManyFlagsValidator flagsValidator, FlagsRepository flagsRepository, CompetitionRepository competitionRepository) {
        this.userRepository = userRepository;
        this.securityUserRepository = securityUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.registrationValidator = registrationValidator;
        this.passwordChangeValidator = passwordChangeValidator;
        this.forgotPasswordValidator = forgotPasswordValidator;
        this.managedByRepository = managedByRepository;
        this.teamValidator = teamValidator;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.sessionUtils = sessionUtils;
        this.competitionRegistrationService = competitionRegistrationService;
        this.flagsValidator = flagsValidator;
        this.flagsRepository = flagsRepository;
        this.competitionRepository = competitionRepository;
    }

    @Override
    public ImportFlagsResultDto importFlags(List<ImportFlag> flags) {
        LOGGER.debug("importFlags({})", flags);
        flagsValidator.validate(flags);
        var role = sessionUtils.getApplicationUserRole();
        if (role != ApplicationUser.Role.CLUB_MANAGER && role != ApplicationUser.Role.TOURNAMENT_MANAGER) {
            throw new ForbiddenException("Not authorized");
        }

        // Grouping because of how entity flags are defined
        Map<String, List<ImportFlag>> groupedByFlagNamesMap = flags.stream().collect(groupingBy(ImportFlag::getFlag));
        var optThisUsersManagedByes = managedByRepository.findAllByManagerIs(sessionUtils.getSessionUser());
        List<ManagedBy> thisUsersManagedByes = null;
        if (optThisUsersManagedByes.isPresent()) {
            thisUsersManagedByes = optThisUsersManagedByes.get();
        }
        if (thisUsersManagedByes != null && !thisUsersManagedByes.isEmpty()) {
            Map<String, ManagedBy> emailToManagedByMap = new HashMap<>();
            for (ManagedBy managedby : thisUsersManagedByes) {
                emailToManagedByMap.put(managedby.getMember().getUser().getEmail(), managedby);
            }
            Set<String> managedEmails = emailToManagedByMap.keySet();
            var errors = new ArrayList<String>();
            for (int i = 0; i < flags.size(); i++) {
                String flagEmailToCheck = flags.get(i).getEmail();
                if (!managedEmails.contains(flagEmailToCheck)) {
                    errors.add(String.format("#%d - %s", i + 1, flagEmailToCheck));
                }
            }

            if (!errors.isEmpty()) {
                throw new ForbiddenListException("Some emails are not managed by you", errors);
            }

            var result = new ImportFlagsResultDto();
            for (var flag : groupedByFlagNamesMap.entrySet()) {
                var flagSet = flag.getValue().stream()
                    .map(ImportFlag::getEmail)
                    .map(emailToManagedByMap::get).collect(Collectors.toSet());
                var flagOpt = flagsRepository.findByName(flag.getKey());
                if (flagOpt.isPresent()) {
                    var clubs = flagOpt.get().getClubs();
                    var initManagedByes = clubs.size();
                    clubs.addAll(flagSet);
                    result.addNewImportedFlags(clubs.size() - initManagedByes);
                } else {
                    flagsRepository.save(new Flags(
                        flag.getKey(),
                        flagSet)
                    );
                    result.addNewImportedFlags(flagSet.size());
                }
            }

            return result;
        } else {
            throw new ForbiddenException("You do not manage anybody yet.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_" + applicationUser.getType().toString());
            return new User(applicationUser.getUser().getEmail(), applicationUser.getUser().getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        Optional<ApplicationUser> applicationUser = userRepository.findApplicationUserByUserEmail(email);
        if (applicationUser.isPresent()) {
            return applicationUser.get();
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public ApplicationUser registerUser(UserRegisterDto userRegisterDto) {
        List<String> errorList = new LinkedList<>();
        LOGGER.debug("Registers user with the given data");
        registrationValidator.validate(userRegisterDto);
        Optional<ApplicationUser> alreadyCreated = userRepository.findApplicationUserByUserEmail(userRegisterDto.getEmail());
        if (alreadyCreated.isPresent()) {
            errorList.add("User with given Email is already registered!");
            throw new ConflictException("Given data conflicts with the data in the system", errorList);
        }
        ApplicationUser applicationUser = userMapper.registerDtoToApplicationUser(userRegisterDto);
        LOGGER.debug("ApplicationUser " + applicationUser);
        LOGGER.debug("ApplicationUser " + applicationUser.toString());
        applicationUser.getUser().setPassword(passwordEncoder.encode(applicationUser.getUser().getPassword()));
        ApplicationUser save = userRepository.save(applicationUser);
        return save;
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        LOGGER.debug("Login user with credentials {}", userLoginDto);
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    @Override
    public Set<Competition> getCompetitionsForCalendar(int year, int weekNumber) {
        LOGGER.debug("getCompetitionsForCalendar(year={}, weekNumber={})", year, weekNumber);
        List<String> errors = new ArrayList<>();
        if (year < 2000) {
            errors.add("Year must be at least 2000");
        }
        if (weekNumber > 52 || weekNumber < 0) {
            errors.add("Number of the week must be from 1 up to 52");
        }
        if (!errors.isEmpty()) {
            throw new ValidationListException("Invalid date requested", errors);
        }

        ApplicationUser user = sessionUtils.getSessionUser();

        var firstMondayOfSelectedYear = LocalDateTime.of(year, 1, 1, 0, 0);
        while (!firstMondayOfSelectedYear.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            firstMondayOfSelectedYear = firstMondayOfSelectedYear.plusDays(1);
        }
        final var beginOfSelectedWeek = firstMondayOfSelectedYear
            .plusWeeks(weekNumber - 1);

        final var endOfSelectedWeek = beginOfSelectedWeek.plusDays(7).minusSeconds(1);

        var competitions = user.getCompetitions();

        var kek = userRepository.findAll();
        var k2k = competitionRepository.findAll();
        if (competitions == null) {
            return Set.of();
        }

        return competitions.stream().filter(
            comp ->
                (comp.getBeginOfCompetition().isAfter(beginOfSelectedWeek)
                    && comp.getBeginOfCompetition().isBefore(endOfSelectedWeek))
                    || (comp.getEndOfCompetition().isBefore(endOfSelectedWeek)
                    && comp.getEndOfCompetition().isAfter(beginOfSelectedWeek))
                    || (comp.getBeginOfCompetition().isBefore(beginOfSelectedWeek)
                    && comp.getEndOfCompetition().isAfter(endOfSelectedWeek))
        ).collect(Collectors.toUnmodifiableSet());
    }

    private void importOneFlag(String flag, ManagedBy managedBy) {
        if (flag == null || flag.isEmpty() || managedBy == null) {
            return;
        }
        var role = sessionUtils.getApplicationUserRole();
        if (role != ApplicationUser.Role.CLUB_MANAGER && role != ApplicationUser.Role.TOURNAMENT_MANAGER) {
            throw new ForbiddenException("Not authorized");
        }

        var flagWithSameName = flagsRepository.findByName(flag);
        if (flagWithSameName.isPresent()) {
            flagWithSameName.get().getClubs().add(managedBy);
        } else {
            flagsRepository.save(new Flags(
                flag,
                Set.of(managedBy)
            ));
        }
    }

    @Override
    public ClubManagerTeamImportResults importTeam(ClubManagerTeamImportDto clubManagerTeamImportDto) {
        if (clubManagerTeamImportDto == null) {
            throw new ValidationException("Team is empty!");
        }
        ApplicationUser clubManager = sessionUtils.getSessionUser();

        ClubManagerTeamImportResults results = new ClubManagerTeamImportResults();
        if (!clubManager.getType().equals(ApplicationUser.Role.CLUB_MANAGER)
            && !clubManager.getType().equals(ApplicationUser.Role.TOURNAMENT_MANAGER)) {
            throw new ForbiddenException("No permissions to import a team");
        }

        teamValidator.validate(clubManagerTeamImportDto);
        ApplicationUser teamMember;
        for (ClubManagerTeamMemberImportDto clubManagerTeamMemberImportDto : clubManagerTeamImportDto.teamMembers()) {
            Optional<ApplicationUser> existingParticipantOpt = userRepository.findApplicationUserByUserEmail(clubManagerTeamMemberImportDto.email());
            if (existingParticipantOpt.isPresent()) {
                teamMember = existingParticipantOpt.get();
                var foundManagedBy = managedByRepository.findByManagerAndMember(clubManager, teamMember);
                if (foundManagedBy.isPresent()) {
                    results.incrementOldParticipantsCount();
                    foundManagedBy.get().setTeamName(clubManagerTeamImportDto.teamName());
                    importOneFlag(clubManagerTeamMemberImportDto.flag(), foundManagedBy.get());
                    foundManagedBy.get().setTeamName(clubManagerTeamImportDto.teamName());
                    continue;
                }
            } else {
                UserRegisterDto participantToRegister =
                    userMapper.clubManagerTeamMemberImportToUserRegisterDto(clubManagerTeamMemberImportDto);
                String generatedPassword = PasswordGenerator.generateRandomPassword(8);
                participantToRegister.setPassword(generatedPassword);
                participantToRegister.setType(ApplicationUser.Role.PARTICIPANT);
                teamMember = registerUser(participantToRegister);
                try {
                    emailService.sendEmail(clubManagerTeamMemberImportDto.email(),
                        String.format("You were automatically registered by your club clubManager\n"
                            + "Use current email and following password to log in: %s", generatedPassword));
                } catch (MessagingException e) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The mail could not been sent with the given properties!");
                } catch (UnsupportedEncodingException e) {
                    throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported encoding for Payload");
                }
            }


            var newManagedBy = managedByRepository.save(
                new ManagedBy(clubManager, teamMember, clubManagerTeamImportDto.teamName())
            );
            importOneFlag(clubManagerTeamMemberImportDto.flag(), newManagedBy);

            results.incrementNewParticipantsCount();
        }

        return results;
    }

    @Override
    public void updateResetPasswordToken(String email, String token) {
        LOGGER.debug("updateResetPasswordToken {}{}", email, token);
        forgotPasswordValidator.validate(new UserPasswordResetRequestDto(email));
        Optional<SecurityUser> toUpdateResetPasswordToken = securityUserRepository.findByEmail(email);
        if (toUpdateResetPasswordToken.isPresent()) {
            SecurityUser temp = toUpdateResetPasswordToken.get();
            temp.setResetPasswordToken(token);
            securityUserRepository.save(temp);
        } else {
            throw new NotFoundException("Could not find User with email: " + email);
        }
    }

    @Override
    public Optional<SecurityUser> getSecurityUserByResetToken(String token) {
        LOGGER.debug("getApplicationUserByResetToken {}", token);
        return securityUserRepository.findSecurityUserByResetPasswordToken(token);
    }

    @Override
    public void updateSecurityUserPassword(SecurityUser toUpdate, String newPassword) {
        LOGGER.debug("updateSecurityUserPassword {}{}", toUpdate, newPassword);
        passwordChangeValidator.validate(new UserCredentialUpdateDto(toUpdate.getEmail(), newPassword));
        String passwordEncoded = passwordEncoder.encode(newPassword);
        toUpdate.setPassword(passwordEncoded);
        toUpdate.setResetPasswordToken(null);
        securityUserRepository.save(toUpdate);
    }

    @Override
    public Optional<SecurityUser> findSecurityUserByEmail(String email) {
        LOGGER.debug("Find security user by email");
        return securityUserRepository.findByEmail(email);
    }

    @Override
    public Set<UserDetailDto> findByUserName(UserSearchDto searchDto) {
        LOGGER.debug("Find User By Name {}", searchDto);
        String name = "";
        Long max = 10L;

        if (searchDto.max() != null
            && searchDto.max() > 0
            && searchDto.max() < 10) {

            max = searchDto.max();
        }

        if (searchDto.name() != null
            && searchDto.name().length() > 0
            && searchDto.name().length() <= 255) {

            name = searchDto.name();
        } else {
            // Nothing to search for
            return Set.of();
        }

        String[] split = name.split(" ");
        String firstName = "";
        String lastName = "";

        if (split.length == 0) {
            // Nothing to search for
            return Set.of();
        } else if (split.length == 1) {
            firstName = split[0];
        } else {
            firstName = split[0];
            lastName = split[1];
        }

        Set<ApplicationUser> result =
            userRepository
                .findApplicationUserByFirstNameStartingWithIgnoreCaseAndLastNameStartingWithIgnoreCase(firstName, lastName)
                .stream().limit(max).collect(Collectors.toSet());

        return userMapper.applicationUserSetToUserDetailDtoSet(result);
    }

    @Override
    public String prepareAndSendPasswordResetMail(UserPasswordResetRequestDto userPasswordResetRequestDto) {
        LOGGER.debug("Prepares a reset password mail and sends it afterwards {}", userPasswordResetRequestDto);
        try {
            String token = RandomString.make(32);
            this.updateResetPasswordToken(userPasswordResetRequestDto.getEmail(), token);
            String resetLink = "http://localhost:4200/#/reset?token=" + token;
            emailService.sendPasswordResetMail(userPasswordResetRequestDto.getEmail(), resetLink);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No entry found for given email: " + userPasswordResetRequestDto.getEmail());
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The mail could not been sent with the given properties!");
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported encoding for Payload");
        }
        return "{\"success\": \"true\"}";
    }

    @Override
    public String resetPassword(UserPasswordResetDto userPasswordResetDto) {
        LOGGER.debug("Resets a password by its reset token{}", userPasswordResetDto);
        String token = userPasswordResetDto.getToken();
        String password = userPasswordResetDto.getPassword();
        Optional<SecurityUser> account = this.getSecurityUserByResetToken(token);

        if (account.isPresent()) {
            SecurityUser temp = account.get();
            this.updateSecurityUserPassword(temp, password);
            return "{\"success\": \"true\"}";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token for password reset");
        }
    }

    @Override
    public String changePassword(UserCredentialUpdateDto userCredentialUpdateDto) {
        LOGGER.debug("Changes a password of the logged in User{}", userCredentialUpdateDto);
        String email = userCredentialUpdateDto.getEmail();
        String password = userCredentialUpdateDto.getPassword();
        Optional<SecurityUser> account = this.findSecurityUserByEmail(email);

        if (account.isPresent()) {
            SecurityUser temp = account.get();
            if (temp.getEmail().equals(sessionUtils.getSessionUser().getUser().getEmail())) {
                this.updateSecurityUserPassword(temp, password);
                return "{\"success\": \"true\"}";
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Changing a password of another user is forbidden!");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid email for password change");
        }
    }

    @Override
    public ResponseParticipantRegistrationDto registerToCompetition(Long id, ParticipantSelfRegistrationDto groupPreference) {
        LOGGER.debug("Registers the logged in user to the competition and its groupPreference {}{}", id, groupPreference);
        if (groupPreference == null) {
            return competitionRegistrationService.selfRegisterParticipant(id, null);
        }
        return competitionRegistrationService.selfRegisterParticipant(id, groupPreference.getGroupPreference());
    }

    @Override
    public UserInfoDto getUser() {
        ApplicationUser user = sessionUtils.getSessionUser();

        return UserInfoDto.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getType())
            .picturePath(user.getPicturePath())
            .build();
    }

    @Override
    public UserDetailDto getUser(Long id) {
        return userMapper.applicationUserToUserDetailDto(userRepository.findById(id).get());
    }
}
