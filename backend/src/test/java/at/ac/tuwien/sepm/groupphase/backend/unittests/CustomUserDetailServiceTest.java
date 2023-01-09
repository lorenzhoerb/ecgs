package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CustomUserDetailServiceTest extends TestDataProvider {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private SecurityUserRepository securityUserRepository;

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

    @Test
    public void findByUserName_NameNull() {
        UserSearchDto searchDto = new UserSearchDto(null, 5L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = customUserDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    @Test
    public void findByUserName_NameEmpty() {
        UserSearchDto searchDto = new UserSearchDto("", 5L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = customUserDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    @Test
    public void findByUserName_NameLongerThan255() {
        UserSearchDto searchDto = new UserSearchDto("A".repeat(256), 5L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = customUserDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    @Test
    public void findByUserName_MaxNull() {
        UserSearchDto searchDto = new UserSearchDto("M", null);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = customUserDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 10);
    }

    @Test
    public void findByUserName_MaxLessThan0() {
        UserSearchDto searchDto = new UserSearchDto("M", -1L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = customUserDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 10);
    }

    @Test
    public void findByUserName_MaxBiggerThan10() {
        UserSearchDto searchDto = new UserSearchDto("M", 100L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = customUserDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 10);
    }
}
