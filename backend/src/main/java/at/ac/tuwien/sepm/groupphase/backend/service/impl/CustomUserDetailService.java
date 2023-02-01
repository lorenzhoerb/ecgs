package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantSelfRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlagsResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCredentialUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailSetFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.FlagsMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.Flags;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenListException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UnauthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.FlagsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.FlagUtils;
import at.ac.tuwien.sepm.groupphase.backend.specification.ApplicationUserSpecs;
import at.ac.tuwien.sepm.groupphase.backend.util.PasswordGenerator;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.ClubManagerTeamImportDtoValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.ForgotPasswordValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.ImportManyFlagsValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.PasswordChangeValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.RegistrationValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.UserSetFlagValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.SimpleFlagValidator;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import java.util.HashSet;
import java.util.Comparator;
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
    private final FlagsMapper flagsMapper;
    private final FlagUtils flagUtils;

    private final EmailService emailService;

    private final ManagedByRepository managedByRepository;
    private final ClubManagerTeamImportDtoValidator teamValidator;
    private final UserSetFlagValidator userSetFlagValidator;
    private final SimpleFlagValidator simpleFlagValidator;

    private final CompetitionRegistrationService competitionRegistrationService;
    private final SessionUtils sessionUtils;

    private final CompetitionRepository competitionRepository;
    private final ImportManyFlagsValidator flagsValidator;
    private final FlagsRepository flagsRepository;

    private final RegisterToRepository registerToRepository;

    private final CompetitionService competitionService;

    @Value("${resetBaseUri}")
    private String resetBaseUri;

    @Autowired
    public CustomUserDetailService(ApplicationUserRepository userRepository,
                                   SecurityUserRepository securityUserRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer,
                                   ManagedByRepository managedByRepository,
                                   RegisterToRepository registerToRepository,
                                   ClubManagerTeamImportDtoValidator teamValidator,
                                   EmailService emailService, UserMapper userMapper, SessionUtils sessionUtils,
                                   UserSetFlagValidator userSetFlagValidator, SimpleFlagValidator simpleFlagValidator,
                                   CompetitionService competitionService,
                                   RegistrationValidator registrationValidator,
                                   PasswordChangeValidator passwordChangeValidator,
                                   ForgotPasswordValidator forgotPasswordValidator,
                                   CompetitionRegistrationService competitionRegistrationService,
                                   ImportManyFlagsValidator flagsValidator,
                                   FlagsRepository flagsRepository, CompetitionRepository competitionRepository,
                                   FlagsMapper flagsMapper) {
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
        this.flagsValidator = flagsValidator;
        this.flagsRepository = flagsRepository;
        this.flagsMapper = flagsMapper;
        this.competitionService = competitionService;
        this.userSetFlagValidator = userSetFlagValidator;
        this.simpleFlagValidator = simpleFlagValidator;
        this.competitionRegistrationService = competitionRegistrationService;
        this.competitionRepository = competitionRepository;
        this.flagUtils = new FlagUtils(flagsMapper, flagsRepository);
        this.registerToRepository = registerToRepository;
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
        var thisUsersManagedByes = managedByRepository.findAllByManagerIs(sessionUtils.getSessionUser());
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
    public String prepareAndSendPasswordResetMail(UserPasswordResetRequestDto userPasswordResetRequestDto) {
        LOGGER.debug("Prepares a reset password mail and sends it afterwards {}", userPasswordResetRequestDto);
        try {
            String token = RandomString.make(32);
            this.updateResetPasswordToken(userPasswordResetRequestDto.getEmail(), token);
            String resetLink = this.resetBaseUri + "/#/reset?token=" + token;
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
        if (!userRegisterDto.getType().equals(ApplicationUser.Role.PARTICIPANT)
            && userRegisterDto.getTeamName() != null) {
            managedByRepository.save(new ManagedBy(
                save, save, userRegisterDto.getTeamName()
            ));
        }

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
        if (!sessionUtils.isAuthenticated()) {
            throw new UnauthorizedException("Not authorized");
        }
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

        Set<Competition> competitions = new HashSet<>();

        // User's registrations
        var registerToListOpt = registerToRepository.findAllByParticipantId(sessionUtils.getSessionUser().getId());
        if (registerToListOpt.isPresent() && !registerToListOpt.get().isEmpty()) {
            for (RegisterTo registerTo : registerToListOpt.get()) {
                Competition toAdd = competitionRepository.findById(registerTo.getGradingGroup().getCompetitions().getId()).get();
                competitions.add(toAdd);
            }
        }

        if (!sessionUtils.isParticipant()) {
            // Registration of those, whom user manages
            for (ManagedBy manageBy : user.getMembers()) {
                if (registerToRepository.findAllByParticipantId(manageBy.getMember().getId()).isPresent()) {
                    List<RegisterTo> registrationList = registerToRepository.findAllByParticipantId(manageBy.getMember().getId()).get();
                    for (RegisterTo registerTo : registrationList) {
                        competitions.add(
                            competitionRepository.findById(registerTo.getGradingGroup().getCompetitions().getId()).get()
                        );
                    }
                }
            }
            // All competitions that are managed by current user
            if (sessionUtils.isCompetitionManager()) {
                competitions.addAll(user.getCompetitions());
            }
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
        LOGGER.debug("importOneFlag({}, {})", flag, managedBy);

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
        LOGGER.debug("importTeam({})", clubManagerTeamImportDto);

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
        var foundManagedByOpt = managedByRepository.findByManagerIdAndMemberId(sessionUtils.getSessionUser().getId(), sessionUtils.getSessionUser().getId());
        if (foundManagedByOpt.isEmpty()) {
            throw new ConflictException("Manager who does not have a team name!");
        }
        String teamName = foundManagedByOpt.get().getTeamName();
        for (ClubManagerTeamMemberImportDto clubManagerTeamMemberImportDto : clubManagerTeamImportDto.teamMembers()) {
            Optional<ApplicationUser> existingParticipantOpt = userRepository.findApplicationUserByUserEmail(clubManagerTeamMemberImportDto.email());
            ApplicationUser teamMember;
            if (existingParticipantOpt.isPresent()) {
                teamMember = existingParticipantOpt.get();
                var foundManagedBy = managedByRepository.findByManagerAndMember(clubManager, teamMember);
                if (foundManagedBy.isPresent()) {
                    results.incrementOldParticipantsCount();
                    foundManagedBy.get().setTeamName(teamName);
                    importOneFlag(clubManagerTeamMemberImportDto.flag(), foundManagedBy.get());
                    continue;
                }
            } else {
                UserRegisterDto participantToRegister =
                    userMapper.clubManagerTeamMemberImportToUserRegisterDto(clubManagerTeamMemberImportDto);
                String generatedPassword = PasswordGenerator.generateRandomPassword(8);
                participantToRegister.setPassword(generatedPassword);
                participantToRegister.setType(ApplicationUser.Role.PARTICIPANT);
                teamMember = registerUser(participantToRegister);
                /*try {
                    emailService.sendEmail(clubManagerTeamMemberImportDto.email(),
                        String.format("You were automatically registered by your club clubManager\n"
                            + "Use current email and following password to log in: %s", generatedPassword));
                } catch (MessagingException e) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The mail could not been sent with the given properties!");
                } catch (UnsupportedEncodingException e) {
                    throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported encoding for Payload");
                }*/
            }


            var newManagedBy = managedByRepository.save(
                new ManagedBy(clubManager, teamMember, teamName)
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
    public String resetPassword(UserPasswordResetDto userPasswordResetDto) {
        LOGGER.debug("Resets a password by its reset token{}", userPasswordResetDto);
        String token = userPasswordResetDto.getToken();
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing token for password reset");
        }
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
        LOGGER.debug("getUser()");
        ApplicationUser user = sessionUtils.getSessionUser();

        return UserInfoDto.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getType())
            .picturePath(user.getPicturePath())
            .build();
    }

    public UserDetailDto getUser(Long id) {
        LOGGER.debug("getUser({})", id);
        return userMapper.applicationUserToUserDetailDto(userRepository.findById(id).get());
    }

    @Override
    public List<SimpleFlagDto> getManagedFlags() {
        LOGGER.debug("getManagedFlags()");

        if (!sessionUtils.isCompetitionManager() && !sessionUtils.isClubManager()) {
            throw new ForbiddenException("Not authorized");
        }

        ApplicationUser sessionUser = sessionUtils.getSessionUser();
        Set<ManagedBy> managedBies = sessionUser.getMembers();
        HashSet<Flags> hs = new HashSet<>();

        for (ManagedBy m : managedBies) {
            hs.addAll(m.getFlags());
        }

        List<Flags> sorted = new ArrayList<>(hs.stream().toList());
        sorted.sort(Comparator.comparing(Flags::getName));

        List<SimpleFlagDto> result = flagsMapper.flagsListToSimpleFlagDtoList(sorted);
        return result == null ? new ArrayList<>() : result;
    }

    private ManagedBy checkUserIsManaged(ApplicationUser sessionUser, ApplicationUser m) {
        LOGGER.debug("checkUserIsManaged({},{})", sessionUser, m);

        if (m == null) {
            throw new ValidationListException("User was null",
                List.of("User was null"));
        }

        Optional<ManagedBy> relOpt = m.getManagers().stream()
            .filter(x -> x.getManager().getId().equals(sessionUser.getId())).findFirst();

        if (relOpt.isEmpty()) {
            throw new ValidationListException("Unmanaged user was passed",
                List.of("Unmanaged user was passed"));
        }

        return relOpt.get();
    }

    @Override
    public void addFlagsForUsers(UserDetailSetFlagDto members) {
        LOGGER.debug("addFlagsForUsers({})", members);

        if (!sessionUtils.isCompetitionManager() && !sessionUtils.isClubManager()) {
            throw new ForbiddenException("Not authorized");
        }

        this.userSetFlagValidator.validate(members);

        List<ApplicationUser> users =
            userRepository.findAllById(members.getUsers().stream().map(UserDetailDto::id).toList());
        Set<Long> myFlagIds = getManagedFlags().stream().map(SimpleFlagDto::id).collect(Collectors.toSet());
        Flags flag = flagUtils.verifyOrCreate(members.getFlag(), myFlagIds);

        ApplicationUser sessionUser = sessionUtils.getSessionUser();
        List<Long> ids = flag.getClubs().stream().map(ManagedBy::getId).toList();

        for (ApplicationUser m : users) {
            ManagedBy rel = checkUserIsManaged(sessionUser, m);

            if (!ids.contains(rel.getId())) {
                flag.getClubs().add(rel);
            }
        }

        flagsRepository.save(flag);
    }


    @Override
    public void removeFlagsForUsers(UserDetailSetFlagDto members) {
        LOGGER.debug("removeFlagsForUsers({})", members);

        if (!sessionUtils.isCompetitionManager() && !sessionUtils.isClubManager()) {
            throw new ForbiddenException("Not authorized");
        }

        this.userSetFlagValidator.validate(members);

        Set<Long> myFlagIds = getManagedFlags().stream().map(SimpleFlagDto::id).collect(Collectors.toSet());
        Flags flag = flagUtils.verify(members.getFlag(), myFlagIds);

        ApplicationUser sessionUser = sessionUtils.getSessionUser();
        List<ApplicationUser> users =
            userRepository.findAllById(members.getUsers().stream().map(UserDetailDto::id).toList());
        List<Long> ids = flag.getClubs().stream().map(ManagedBy::getId).toList();

        for (ApplicationUser m : users) {
            ManagedBy rel = checkUserIsManaged(sessionUser, m);

            if (ids.contains(rel.getId())) {
                flag.getClubs().removeIf(x -> x.getId().equals(rel.getId()));
            }
        }

        flagsRepository.save(flag);
    }

    @Override
    public Page<UserDetailFlagDto> getMembers(UserDetailFilterDto filter) {
        LOGGER.debug("List members");

        if (sessionUtils.getApplicationUserRole() != ApplicationUser.Role.CLUB_MANAGER
            && sessionUtils.getApplicationUserRole() != ApplicationUser.Role.TOURNAMENT_MANAGER) {
            throw new ForbiddenException("No Permission to get participants with flags");
        }

        int page = 0;
        int size = 10;

        if (filter == null) {
            filter = new UserDetailFilterDto();
        }

        if (filter.getPage() != null && filter.getPage() >= 0) {
            page = filter.getPage();
        }

        if (filter.getSize() != null && filter.getSize() >= 0) {
            size = filter.getSize();
        }

        ApplicationUser applicationUser = sessionUtils.getSessionUser();

        Specification<ApplicationUser> specification = ApplicationUserSpecs.specsForMembers(applicationUser.getId(), filter);
        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationUser> partPage = userRepository.findAll(specification, pageable);

        List<UserDetailFlagDto> result = new ArrayList<>();

        for (ApplicationUser a : partPage.getContent()) {
            UserDetailFlagDto curr = userMapper.applicationUserToUserDetailFlagDto(a);
            List<Flags> flags = a.getManagers().stream()
                .filter(m -> m.getManager().getId().equals(applicationUser.getId()))
                .findFirst()
                .orElseThrow(() -> new ConflictException("No Manager set", List.of("No Manager set")))
                .getFlags()
                .stream()
                .toList();
            curr.setFlags(flagsMapper.flagsListToSimpleFlagDtoList(flags));
            result.add(curr);
        }

        return new PageImpl<>(result, pageable, partPage.getTotalElements());
    }

}
