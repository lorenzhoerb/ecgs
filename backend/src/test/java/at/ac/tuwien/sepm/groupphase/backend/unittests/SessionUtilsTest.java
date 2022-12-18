package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class SessionUtilsTest extends TestDataProvider {

    @Autowired
    private SessionUtils sessionUtils;
    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @BeforeEach
    public void beforeEach() {
        applicationUserRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = TEST_USER_TOURNAMENT_MANAGER_EMAIL)
    public void givenLoggedInCompUser_whenGettingSessionUser_expectUserEntity() {
        UserRegisterDto userExpected = getValidRegistrationDtoForCompetitionManager();
        setUpCompetitionUser();
        ApplicationUser sessionUser = sessionUtils.getSessionUser();
        assertAll(
            () -> assertNotNull(sessionUser),
            () -> assertEquals(userExpected.getEmail(), sessionUser.getUser().getEmail()),
            () -> assertEquals(userExpected.getType(), sessionUser.getType()),
            () -> assertEquals(userExpected.getFirstName(), sessionUser.getFirstName()),
            () -> assertEquals(userExpected.getLastName(), sessionUser.getLastName())
        );
    }

    @Test
    public void givenNotLoggedInUser_whenCheckingRole_expectFalse() {
        assertAll(
            () -> assertFalse(sessionUtils.isParticipant()),
            () -> assertFalse(sessionUtils.isClubManager()),
            () -> assertFalse(sessionUtils.isCompetitionManager())
        );
    }

    @Test
    @WithMockUser(username = TEST_USER_TOURNAMENT_MANAGER_EMAIL)
    public void givenLoggedInCompUser_whenCheckingRole_expectTrue() {
        setUpCompetitionUser();
        assertTrue(sessionUtils.isCompetitionManager());
    }

    @Test
    @WithMockUser(username = TEST_USER_TOURNAMENT_MANAGER_EMAIL)
    public void givenLoggedInUserAndNoUserInPersistence_whenGettingSessionUser_expectRuntimeException() {
        assertThrows(RuntimeException.class, () -> sessionUtils.getSessionUser());
    }

    @Test
    @WithMockUser(username = TEST_USER_TOURNAMENT_MANAGER_EMAIL)
    public void givenLoggedInCompUser_whenGettingSessionRole_expectCompRole() {
        setUpCompetitionUser();
        assertEquals(ApplicationUser.Role.TOURNAMENT_MANAGER, sessionUtils.getApplicationUserRole());
    }

    @Test
    public void givenNotLoggedInUser_whenGettingSessionRole_expectNull() {
        assertNull(sessionUtils.getApplicationUserRole());
    }

    @Test
    @WithMockUser(username = TEST_USER_TOURNAMENT_MANAGER_EMAIL)
    public void givenLoggedInCompUser_whenCheckingCompUser_checkRoles() {
        setUpCompetitionUser();
        assertTrue(sessionUtils.isCompetitionManager());
        assertFalse(sessionUtils.isClubManager());
        assertFalse(sessionUtils.isParticipant());
    }

    @Test
    public void givenNotLoggedInUser_checkingRoles() {
        setUpCompetitionUser();
        assertFalse(sessionUtils.isCompetitionManager());
        assertFalse(sessionUtils.isClubManager());
        assertFalse(sessionUtils.isParticipant());
    }

    @Test
    public void testSetSessionUserEmail() {
        sessionUtils.setSessionUserEmail(TEST_USER_TOURNAMENT_MANAGER_EMAIL);
        UserRegisterDto userRegisterDto = getValidRegistrationDtoForCompetitionManager();
        setUpCompetitionUser();
        assertAll(
            () -> assertNotNull(sessionUtils.getSessionUser()),
            () -> assertEquals(ApplicationUser.Role.TOURNAMENT_MANAGER, sessionUtils.getApplicationUserRole()),
            () -> assertEquals(sessionUtils.getSessionUser().getFirstName(), userRegisterDto.getFirstName()),
            () -> assertEquals(sessionUtils.getSessionUser().getLastName(), userRegisterDto.getLastName()),
            () -> assertEquals(sessionUtils.getSessionUser().getUser().getEmail(), userRegisterDto.getEmail())
        );
        sessionUtils.setSessionUserEmail(null);
    }
}
