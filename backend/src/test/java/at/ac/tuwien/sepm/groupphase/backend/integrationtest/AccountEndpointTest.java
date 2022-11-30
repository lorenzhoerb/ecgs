package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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


    private ApplicationUser user;


    @BeforeEach
    public void beforeEach() {
        applicationUserRepository.deleteAll();
    }

    @Test
    @Transactional
    public void createAccountShouldSuccess() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setFirstName("Hans").setLastName("Meyer").setEmail("hans.meyer@gmail.com").setPassword("password187")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.ClubManager)
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
    }


    @Test
    @Transactional
    public void createAccountShouldFailMissingName() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setLastName("Mey$$er").setEmail("hans.meyer@gmail.com").setPassword("password187")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.ClubManager)
                .setDateOfBirth(new GregorianCalendar(1998, Calendar.FEBRUARY, 11).getTime()).createUserRegisterDto();

        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    @Transactional
    public void createAccountShouldFailWrongPassword() throws Exception {
        UserRegisterDto userRegisterDto =
            new UserRegisterDto.UserRegisterDtoBuilder().setFirstName("Test Hab").setLastName("Meyer").setEmail("hans.meyer@gmail.com").setPassword("1234")
                .setGender(
                    ApplicationUser.Gender.MALE).setType(ApplicationUser.Role.ClubManager)
                .setDateOfBirth(new GregorianCalendar(1998, Calendar.FEBRUARY, 11).getTime()).createUserRegisterDto();

        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = mockMvc.perform(post(ACCOUNT_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @AfterEach
    public void afterEach() {
        applicationUserRepository.deleteAll();
    }
}
