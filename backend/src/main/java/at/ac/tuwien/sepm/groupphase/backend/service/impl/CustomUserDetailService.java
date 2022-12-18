package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCredentialUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.validation.ForgotPasswordValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.PasswordChangeValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.RegistrationValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import at.ac.tuwien.sepm.groupphase.backend.util.PasswordGenerator;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.ClubManagerTeamImportDtoValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final ManagedByRepository managedByRepository;

    private final UserValidator userValidator;
    private final ClubManagerTeamImportDtoValidator teamValidator;

    private final EmailService emailService;

    private final SessionUtils sessionUtils;


    @Autowired
    public CustomUserDetailService(ApplicationUserRepository userRepository,
                                   SecurityUserRepository securityUserRepository, PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer, ManagedByRepository managedByRepository,
                                   UserValidator userValidator, ClubManagerTeamImportDtoValidator teamValidator,
                                   EmailService emailService, UserMapper userMapper, SessionUtils sessionUtils,
            RegistrationValidator validator,
            PasswordChangeValidator passwordChangeValidator, ForgotPasswordValidator forgotPasswordValidator) {
        this.userRepository = userRepository;
        this.securityUserRepository = securityUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.registrationValidator = validator;
        this.passwordChangeValidator = passwordChangeValidator;
        this.forgotPasswordValidator = forgotPasswordValidator;
        this.managedByRepository = managedByRepository;
        this.userValidator = userValidator;
        this.teamValidator = teamValidator;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.sessionUtils = sessionUtils;
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
        userValidator.validateRegistration(applicationUser);
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
    public Set<Competition> getCompetitionsForCalendar(ApplicationUser user, int year, int weekNumber) {
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

        var firstMondayOfSelectedYear = LocalDateTime.of(year, 1, 1, 0, 0);
        while (!firstMondayOfSelectedYear.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            firstMondayOfSelectedYear = firstMondayOfSelectedYear.plusDays(1);
        }
        final var beginOfSelectedWeek = firstMondayOfSelectedYear
            .plusWeeks(weekNumber - 1);

        final var endOfSelectedWeek = beginOfSelectedWeek.plusDays(7).minusSeconds(1);

        Optional<ApplicationUser> userOptional = userRepository.findById(user.getId());
        if (userOptional.isEmpty()) {
            // @TODO: this must never happen please don't pass entitiys to the service
            // layer
            return null;
        }
        user = userOptional.get();

        var competitions = user.getCompetitions();
        if (competitions == null) {
            return null;
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

    @Override
    public ClubManagerTeamImportResults importTeam(ApplicationUser clubManager, ClubManagerTeamImportDto clubManagerTeamImportDto) {
        if (clubManagerTeamImportDto == null) {
            throw new ValidationException("Team is empty!");
        }

        int addedParticipants = 0;
        int alreadyManagedParticipants = 0;
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
                if (managedByRepository.findByManagerAndMember(clubManager, teamMember).isPresent()) {
                    alreadyManagedParticipants++;
                    continue;
                }
            } else {
                // register
                UserRegisterDto participantToRegister =
                    userMapper.clubManagerTeamMemberImportToUserRegisterDto(clubManagerTeamMemberImportDto);
                String generatedPassword = PasswordGenerator.generateRandomPassword(8);
                participantToRegister.setPassword(generatedPassword);
                participantToRegister.setType(ApplicationUser.Role.PARTICIPANT);
                teamMember = registerUser(participantToRegister);

                emailService.sendEmail(clubManagerTeamMemberImportDto.email(),
                    String.format("You were automatically registered by your club clubManager\n"
                    + "Use current email and following password to log in: %s", generatedPassword));

            }


            managedByRepository.save(
                new ManagedBy(clubManager, teamMember, clubManagerTeamImportDto.teamName())
            );

            addedParticipants++;
        }

        return new ClubManagerTeamImportResults(
            addedParticipants,
            alreadyManagedParticipants
        );
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
        userValidator.validatePasswordUpdate(newPassword);
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
}
