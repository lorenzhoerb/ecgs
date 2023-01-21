package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.FileInputException;
import at.ac.tuwien.sepm.groupphase.backend.service.PictureService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class PictureServiceTest extends TestDataProvider {
    Path path1, path2;
    File file1, file2;

    /* This directory and the files created in it will be deleted after
     * tests are run, even in the event of failures or exceptions.
     */

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    PictureService pictureService;

    @Autowired
    private ResourceLoader resourceLoader;



    @BeforeEach
    public void setUp() {
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    void givenMultiPartFileWithWrongExtensionTxt_whenWriteUserPicture_thenFailureCauseOfWrongExtension() throws IOException{
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
    void givenMultiPartFileWithWrongExtensionExe_whenWriteUserPicture_thenFailureCauseOfWrongExtension() throws IOException{
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
    void givenMultiPartFileFromTemplate_whenWriteUserPicture_thenSuccessAndContentFits() throws IOException{
        ApplicationUser createdOne = setUpCompetitionUser();
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
}
