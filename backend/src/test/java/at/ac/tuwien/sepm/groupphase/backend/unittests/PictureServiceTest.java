package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.FileInputException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.PictureService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class PictureServiceTest extends TestDataProvider {

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    PictureService pictureService;

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    CompetitionService competitionService;

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    private CompetitionDetailDto competitionDetailDto;


    @BeforeEach
    public void setUp() {
        setUpCompetitionUser();
        setUpAlternateCompetitionUser("compmanager@email.com", "12345678");
        competitionDetailDto = competitionService.create(getValidCompetitionDetailDto());
    }

    @AfterEach
    public void cleanUp(){
        competitionRepository.deleteAll();
        applicationUserRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    void givenMultiPartFileWithWrongExtensionTxt_whenWriteUserPicture_thenFailureCauseOfWrongExtension() throws IOException {
        MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
        );

        assertThrows(FileInputException.class, () -> {
            pictureService.saveUserPicture(file);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    void givenMultiPartFileWithWrongExtensionExe_whenWriteUserPicture_thenFailureCauseOfWrongExtension() throws IOException {
        MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "hello.exe",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
        );

        assertThrows(FileInputException.class, () -> {
            pictureService.saveUserPicture(file);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    void givenMultiPartFileFromTemplate_whenWriteUserPicture_thenSuccessAndContentFits() throws IOException {
        ApplicationUser createdOne = applicationUserRepository.findById(sessionUtils.getSessionUser().getId()).get();
        Resource originalSource = resourceLoader.getResource("classpath:/user-pictures/dot.png");
        byte[] originalBytes = originalSource.getInputStream().readAllBytes();
        MockMultipartFile file
            = new MockMultipartFile(
            "hello",
            "hello.png",
            MediaType.IMAGE_PNG_VALUE,
            originalBytes
        );

        String writtenFileTo = pictureService.saveUserPicture(file);
        Path path = Paths.get(writtenFileTo);
        Resource loadedSource = resourceLoader.getResource(path.toString());


        assertAll(
            () -> assertTrue("File should exist", Files.exists(path)),
            () -> assertEquals(createdOne.getId() + ".png", loadedSource.getFile().getName()));

        Files.delete(path);
        Files.delete(path.getParent());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    void givenMultiPartFileWithWrongExtensionTxt_whenWriteCompetitionPicture_thenFailureCauseOfWrongExtension() throws IOException {
        MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
        );

        assertThrows(FileInputException.class, () -> {
            pictureService.saveCompetitionImage(competitionDetailDto.getId(), file);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    void givenMultiPartFileWithWrongExtensionExe_whenWriteCompetitionPicture_thenFailureCauseOfWrongExtension() throws IOException {
        MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "hello.exe",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
        );

        assertThrows(FileInputException.class, () -> {
            pictureService.saveCompetitionImage(competitionDetailDto.getId(), file);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    void givenMultiPartFileFromTemplate_whenWriteCompetitionPicture_thenSuccessAndContentFits() throws IOException {
        Competition createdOne = competitionRepository.findById(competitionDetailDto.getId()).get();
        Resource originalSource = resourceLoader.getResource("classpath:/competition-pictures/turnier.jpg");
        byte[] originalBytes = originalSource.getInputStream().readAllBytes();
        MockMultipartFile file
            = new MockMultipartFile(
            "turnier",
            "turnier.jpg",
            MediaType.IMAGE_PNG_VALUE,
            originalBytes
        );

        String writtenFileTo = pictureService.saveCompetitionImage(competitionDetailDto.getId(), file);
        Path path = Paths.get(writtenFileTo);
        Resource loadedSource = resourceLoader.getResource(path.toString());


        assertAll(
            () -> assertTrue("File should exist", Files.exists(path)),
            () -> assertEquals(createdOne.getId() + ".jpg", loadedSource.getFile().getName()));

        Files.delete(path);
        Files.delete(path.getParent());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    void givenMultipartFileFromTemplate_whenTryToWriteCompetitionPictureAsWrongCreator_expectForbiddenException() throws IOException{
        Resource originalSource = resourceLoader.getResource("classpath:/competition-pictures/turnier.jpg");
        byte[] originalBytes = originalSource.getInputStream().readAllBytes();
        MockMultipartFile file
            = new MockMultipartFile(
            "turnier",
            "turnier.jpg",
            MediaType.IMAGE_PNG_VALUE,
            originalBytes
        );

        UserDetails userDetails = new User("compmanager@email.com", "12345678", Arrays.asList(new SimpleGrantedAuthority("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)));
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThrows(ForbiddenException.class, () -> {
            pictureService.saveCompetitionImage(competitionDetailDto.getId(), file);
        });

        SecurityContextHolder.clearContext();
    }
}
