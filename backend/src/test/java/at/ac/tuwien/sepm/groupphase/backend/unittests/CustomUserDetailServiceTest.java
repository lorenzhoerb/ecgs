package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CustomUserDetailServiceTest extends TestDataProvider {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @BeforeEach
    public void beforeEach() {
        applicationUserRepository.deleteAll();
        setUpCompetitionUser();
    }

    @Test
    public void registerValidUser_thenGettingItByEmail_shouldSuccess() {
        customUserDetailService.registerUser(getValidRegistrationDtoForParticipant());
        UserDetails gotFromDB = customUserDetailService.loadUserByUsername("basic@email.com");
        assertNotNull(gotFromDB);
        assertEquals(gotFromDB.getUsername(), "basic@email.com");
    }

    @Test
    public void loginParticipantUser_thenGettingBackAuthToken_shouldSuccess() {
        String authToken = null;
        authToken = customUserDetailService.login(UserLoginDto.UserLoginDtoBuilder.anUserLoginDto().withEmail("comp.manager@email.com").withPassword("12345678").build());
        assertNotNull(authToken);
        assertTrue(authToken.startsWith("Bearer "));
    }

    protected UserRegisterDto getValidRegistrationDtoForParticipant() {
        return new UserRegisterDto(
            TEST_USER_BASIC_EMAIL,
            "123456789",
            "Firsty",
            "Testing",
            ApplicationUser.Gender.MALE,
            new Date(),
            ApplicationUser.Role.PARTICIPANT);
    }
}
