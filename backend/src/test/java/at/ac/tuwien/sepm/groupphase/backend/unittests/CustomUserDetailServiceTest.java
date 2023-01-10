package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.FlagsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CustomUserDetailServiceTest extends TestDataProvider {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private SecurityUserRepository securityUserRepository;

    @Autowired
    private UserService userDetailService;

    @Autowired
    private FlagsRepository flagsRepository;

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
        userDetailService.registerUser(getValidRegistrationDtoForParticipant());
        UserDetails gotFromDB = userDetailService.loadUserByUsername("basic@email.com");
        assertNotNull(gotFromDB);
        assertEquals(gotFromDB.getUsername(), "basic@email.com");
    }

    @Test
    public void loginParticipantUser_thenGettingBackAuthToken_shouldSuccess() {
        String authToken = null;
        authToken = userDetailService.login(UserLoginDto.UserLoginDtoBuilder.anUserLoginDto().withEmail("comp.manager@email.com").withPassword("12345678").build());
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

        Set<UserDetailDto> result = userDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    @Test
    public void findByUserName_NameEmpty() {
        UserSearchDto searchDto = new UserSearchDto("", 5L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = userDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    @Test
    public void findByUserName_NameLongerThan255() {
        UserSearchDto searchDto = new UserSearchDto("A".repeat(256), 5L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = userDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 0);
    }

    @Test
    public void findByUserName_MaxNull() {
        UserSearchDto searchDto = new UserSearchDto("M", null);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = userDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 10);
    }

    @Test
    public void findByUserName_MaxLessThan0() {
        UserSearchDto searchDto = new UserSearchDto("M", -1L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = userDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 10);
    }

    @Test
    public void findByUserName_MaxBiggerThan10() {
        UserSearchDto searchDto = new UserSearchDto("M", 100L);

        setupRandomApplicationUsers(applicationUserRepository,
            securityUserRepository);

        Set<UserDetailDto> result = userDetailService.findByUserName(searchDto);

        assertNotNull(result);
        assertEquals(result.size(), 10);
    }

    @Test
    @WithMockUser(username = "cm_1@test.test")
    public void importFlags_whenAllParticipantsArePresent_shouldAddOnlyOneFlag() throws Exception {
        var initFlagsNumber =  StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var testFlags = flagsImport_setupTestFlags();
        userDetailService.importFlags(testFlags);

        var resultingFlagsNumber =  StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        assertThat(resultingFlagsNumber - initFlagsNumber).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "cm_1@test.test")
    public void importFlags_addFlagForExistingButNotManagedParticipant_shouldThrowValidationExceptionAndNothingMore() throws Exception {
        userDetailService.registerUser(new UserRegisterDto(
            "nonexisting@alo.com",
            "rootroot",
            "fnnnnn",
            "lnnnnn",
            ApplicationUser.Gender.MALE,
            new Date(0L),
            ApplicationUser.Role.PARTICIPANT
        ));
        flagsImport_setupTestFlags();
        ForbiddenListException vle_exception = assertThrows(ForbiddenListException.class, () -> {
            userDetailService.importFlags(new ArrayList<>() {
                    {
                        add(new ImportFlag(
                            "nonexisting@alo.com",
                            "cool")
                        );
                    }
                });
            }
        );
        assertThat(vle_exception.getMessage()).contains("Some emails are not managed by you");
        assertThat(vle_exception.errors().get(0))
            .contains("#1 - nonexisting@alo.com");
    }

    @Test
    @WithMockUser(username = "cm_1@test.test")
    public void importFlags_addFlagForNonExistingParticipant_shouldThrowValidationExceptionAndNothingMore() throws Exception {
        userDetailService.registerUser(new UserRegisterDto(
            "nonexisting@alo.com",
            "rootroot",
            "fnnnnn",
            "lnnnnn",
            ApplicationUser.Gender.MALE,
            new Date(0L),
            ApplicationUser.Role.PARTICIPANT
        ));
        flagsImport_setupTestFlags();
        ForbiddenListException vle_exception = assertThrows(ForbiddenListException.class, () -> {
            userDetailService.importFlags(new ArrayList<>() {
                    {
                        add(new ImportFlag(
                            "nonexisting@alo.com",
                            "cool")
                        );
                    }
                });
            }
        );
        assertThat(vle_exception.getMessage()).contains("Some emails are not managed by you");
        assertThat(vle_exception.errors().get(0))
            .contains("#1 - nonexisting@alo.com");
    }
}
