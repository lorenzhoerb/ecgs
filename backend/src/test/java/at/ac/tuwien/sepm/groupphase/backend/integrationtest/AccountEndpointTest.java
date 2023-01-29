package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCredentialUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AccountEndpointTest implements TestData {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionUtils sessionUtils;


    @BeforeEach
    public void beforeEach() {
        applicationUserRepository.deleteAll();
    }

    @Test
    @Transactional
    public void createAccount_withValidDto_shouldSuccess_expect201() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setFirstName("Hans").setLastName("Meyer").setEmail("hans.meyer@gmail.com").setPassword("password187")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.CLUB_MANAGER)
                .setDateOfBirth(new GregorianCalendar(1998, Calendar.FEBRUARY, 11).getTime()).createUserRegisterDto();

        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        Optional<ApplicationUser> accountOption = applicationUserRepository.findApplicationUserByUserEmail(userRegisterDto.getEmail());
        assertTrue(accountOption.isPresent());
        ApplicationUser account = accountOption.get();
        LOGGER.warn("" + account.getUser().toString());

        assertFalse(Objects.isNull(account.getId()));
        assertEquals(account.getFirstName(), userRegisterDto.getFirstName());
        assertEquals(account.getLastName(), userRegisterDto.getLastName());
        assertEquals(account.getUser().getEmail(), userRegisterDto.getEmail());
        assertTrue(passwordEncoder.matches(userRegisterDto.getPassword(), account.getUser().getPassword()));
    }


    @Test
    @Transactional
    public void createAccount_withMissingName_shouldFail_expect422() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setLastName("Mey$$er").setEmail("hans.meyer@gmail.com").setPassword("password187")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.CLUB_MANAGER)
                .setDateOfBirth(new GregorianCalendar(1998, Calendar.FEBRUARY, 11).getTime()).createUserRegisterDto();

        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }


    @Test
    @Transactional
    public void createAccount_withWrongPassword_shouldFail_expect422() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setFirstName("Test Hab").setLastName("Meyer").setEmail("hans.meyer@gmail.com").setPassword("1234")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.CLUB_MANAGER)
                .setDateOfBirth(new GregorianCalendar(1998, Calendar.FEBRUARY, 11).getTime()).createUserRegisterDto();

        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void registerUser_thenLogInAsUser_thenRequestPasswordReset_andReset_expect200() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setFirstName("Hans").setLastName("Meyer").setEmail("hans.meyer@gmail.com").setPassword("password187")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.CLUB_MANAGER)
                .setDateOfBirth(new GregorianCalendar(1998, Calendar.FEBRUARY, 11).getTime()).createUserRegisterDto();

        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        UserPasswordResetRequestDto userPasswordResetRequestDto = new UserPasswordResetRequestDto();
        userPasswordResetRequestDto.setEmail("hans.meyer@gmail.com");

        body = objectMapper.writeValueAsString(userPasswordResetRequestDto);

        mvcResult = mockMvc.perform(post(ACCOUNT_REQUEST_PASSWORD_RESET_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String token = applicationUserRepository.findApplicationUserByUserEmail("hans.meyer@gmail.com").get().getUser().getResetPasswordToken();

        UserPasswordResetDto userPasswordResetDto = new UserPasswordResetDto();
        userPasswordResetDto.setToken(token);
        userPasswordResetDto.setPassword(passwordEncoder.encode("pass123456"));

        body = objectMapper.writeValueAsString(userPasswordResetDto);

        mvcResult = mockMvc.perform(post(ACCOUNT_RESET_PASSWORD_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void requestPasswordReset_withInvalidEmail_expect404() throws Exception {
        UserPasswordResetRequestDto userPasswordResetRequestDto = new UserPasswordResetRequestDto();
        userPasswordResetRequestDto.setEmail("hans.meyer@gmail.com");

        String body = objectMapper.writeValueAsString(userPasswordResetRequestDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REQUEST_PASSWORD_RESET_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void tryResetPassword_withMissingToken_expect404() throws Exception {
        UserPasswordResetDto userPasswordResetDto = new UserPasswordResetDto();
        userPasswordResetDto.setToken(null);
        userPasswordResetDto.setPassword(passwordEncoder.encode("pass123456"));

        String body = objectMapper.writeValueAsString(userPasswordResetDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_RESET_PASSWORD_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @Transactional
    public void registerUser_thenLogInAsUser_thenChangePassword_expect200() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setFirstName("Hans").setLastName("Meyer").setEmail("hans.meyer@gmail.com").setPassword("password187")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.CLUB_MANAGER)
                .setDateOfBirth(new GregorianCalendar(1998, Calendar.FEBRUARY, 11).getTime()).createUserRegisterDto();

        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        UserLoginDto userLoginDto =
            UserLoginDto.UserLoginDtoBuilder.anUserLoginDto().withEmail(userRegisterDto.getEmail()).withPassword(userRegisterDto.getPassword()).build();

        body = objectMapper.writeValueAsString(userLoginDto);

        mvcResult = mockMvc.perform(post(ACCOUNT_LOGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        UserCredentialUpdateDto userCredentialUpdateDto = new UserCredentialUpdateDto();
        userCredentialUpdateDto.setPassword("pass1234!");
        userCredentialUpdateDto.setEmail("hans.meyer@gmail.com");

        body = objectMapper.writeValueAsString(userCredentialUpdateDto);
        mvcResult = mockMvc.perform(post(ACCOUNT_CHANGE_PASSWORD_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("hans.meyer@gmail.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        Optional<ApplicationUser> accountOption = applicationUserRepository.findApplicationUserByUserEmail(userCredentialUpdateDto.getEmail());
        assertTrue(accountOption.isPresent());
        ApplicationUser account = accountOption.get();
        LOGGER.warn("" + account.getUser().toString());
        assertFalse(Objects.isNull(account.getId()));
        assertEquals(account.getUser().getEmail(), userCredentialUpdateDto.getEmail());
        assertTrue(passwordEncoder.matches(userCredentialUpdateDto.getPassword(), account.getUser().getPassword()));
    }

    @Test
    public void changePassword_whenNotLoggedIn_expect403() throws Exception {
        UserCredentialUpdateDto userCredentialUpdateDto = new UserCredentialUpdateDto();
        userCredentialUpdateDto.setPassword("pass1234!");
        userCredentialUpdateDto.setEmail("hans.meyer@gmail.com");

        String body = objectMapper.writeValueAsString(userCredentialUpdateDto);
        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_CHANGE_PASSWORD_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void changePassword_whenLoggedIn_forAnotherUser_expect403() throws Exception {
        //create the user which the authorizarion token will contain
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setFirstName("Hans").setLastName("Meyer").setEmail("hans.meyer@gmail.com").setPassword("password187")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.CLUB_MANAGER)
                .setDateOfBirth(new GregorianCalendar(1998, Calendar.FEBRUARY, 11).getTime()).createUserRegisterDto();

        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        //create another user
        userRegisterDto.setEmail("not_correct@mail.com");

        body = objectMapper.writeValueAsString(userRegisterDto);

        mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        UserCredentialUpdateDto userCredentialUpdateDto = new UserCredentialUpdateDto();
        userCredentialUpdateDto.setPassword("pass1234!");
        userCredentialUpdateDto.setEmail("not_correct@mail.com");

        body = objectMapper.writeValueAsString(userCredentialUpdateDto);
        mvcResult = mockMvc.perform(post(ACCOUNT_CHANGE_PASSWORD_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("hans.meyer@gmail.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }


    @AfterEach
    public void afterEach() {
        applicationUserRepository.deleteAll();
    }
}
