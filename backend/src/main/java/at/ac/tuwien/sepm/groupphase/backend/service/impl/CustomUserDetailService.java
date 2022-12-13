package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.util.Validator;
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
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationUserRepository userRepository;
    private final SecurityUserRepository securityUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final Validator validator;

    private final UserMapper userMapper;

    @Autowired
    public CustomUserDetailService(ApplicationUserRepository userRepository, SecurityUserRepository securityUserRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer, Validator validator, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.securityUserRepository = securityUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.validator = validator;
        this.userMapper = userMapper;
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
        LOGGER.debug("Registers user with the given data");
        ApplicationUser applicationUser = userMapper.registerDtoToApplicationUser(userRegisterDto);
        LOGGER.debug("ApplicationUser " + applicationUser);
        LOGGER.debug("ApplicationUser " + applicationUser.toString());
        validator.validateRegistration(applicationUser);
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
    public void updateResetPasswordToken(String email, String token) {
        LOGGER.debug("updateResetPasswordToken {}{}", email, token);
        //Optional<ApplicationUser> toUpdateResetPasswordToken = userRepository.findApplicationUserByUserEmail(email);
        Optional<SecurityUser> toUpdateResetPasswordToken = securityUserRepository.findByEmail(email);
        Optional<SecurityUser> tmp = securityUserRepository.findById(2L);
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
        validator.validatePasswordUpdate(newPassword);
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
