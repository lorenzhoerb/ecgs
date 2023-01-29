package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PictureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class PictureServiceSecurityTest extends TestDataProvider {
    @Autowired
    private PictureService pictureService;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @BeforeEach
    public void setUp() {
        applicationUserRepository.deleteAll();
        setUpClubManagerUser();
        setUpParticipantUser();
    }

    @AfterEach
    public void cleanUp(){
        applicationUserRepository.deleteAll();
    }

    @Test
    public void givenUnauthenticatedUser_WhenUploadingUserImage_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            pictureService.saveUserPicture(null);
        });
    }

    @Test
    public void givenUnauthenticatedUser_WhenUploadingCompetitionImage_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            pictureService.saveCompetitionImage(null, null);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void givenAuthenticatedParticipant_WhenUploadingCompetitionImage_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            pictureService.saveCompetitionImage(null, null);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_CLUB_MANAGER_EMAIL)
    public void givenAuthenticatedClubManager_WhenUploadingCompetitionImage_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            pictureService.saveCompetitionImage(null, null);
        });
    }
}
