package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.FileInputException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PictureService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class PictureServiceImpl implements PictureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SessionUtils sessionUtils;

    private final ApplicationUserRepository applicationUserRepository;

    private final CompetitionRepository competitionRepository;

    @Value("${userPictureFolder}")
    private String userPicturesFolder;

    @Value("${competitionPictureFolder}")
    private String competitionPicturesFolder;

    public PictureServiceImpl(SessionUtils sessionUtils, ApplicationUserRepository applicationUserRepository, CompetitionRepository competitionRepository) {
        this.sessionUtils = sessionUtils;
        this.applicationUserRepository = applicationUserRepository;
        this.competitionRepository = competitionRepository;
    }

    @Override
    public String saveUserPicture(MultipartFile multipartFile) {
        LOGGER.debug("saveUserPicture {}", multipartFile);
        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("No permission to set a picture for your"
                + " user, when not logged in!");
        }
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg")) {
            ApplicationUser toSaveFor = applicationUserRepository.findById(sessionUtils.getSessionUser().getId()).get();
            Path userFolder = Paths.get(userPicturesFolder);
            if (!Files.exists(userFolder)) {
                try {
                    Files.createDirectories(userFolder);
                } catch (IOException e) {
                    throw new FileInputException(e);
                }
            }

            String filePath = userFolder.toString() + "/" + toSaveFor.getId() + "." + extension;

            try (InputStream inputStream = multipartFile.getInputStream()) {
                if (toSaveFor.getPicturePath() != null) {
                    if (Files.exists(Paths.get(toSaveFor.getPicturePath()))) {
                        Files.delete(Paths.get(toSaveFor.getPicturePath()));
                    }
                }
                writeImage(filePath, inputStream);
                toSaveFor.setPicturePath(filePath);
                applicationUserRepository.save(toSaveFor);
                return filePath;
            } catch (IOException ioException) {
                throw new FileInputException("Could not save user image file: " + multipartFile.getOriginalFilename(), ioException);
            }
        } else {
            throw new FileInputException("Wrong file format");
        }
    }

    @Override
    public String saveCompetitionImage(Long id, MultipartFile multipartFile) {
        LOGGER.debug("saveCompetitionPicture {}", multipartFile);
        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("No permission to set a picture for a competition, when not logged in!");
        }
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("You need to be Competition Manager to set a picture for a competition");
        }
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg")) {
            if (competitionRepository.findById(id).isEmpty()) {
                throw new NotFoundException("Could not find competition with id: " + id + " to set the picture for.");
            }
            Competition toSaveFor = competitionRepository.findById(id).get();

            if (!toSaveFor.getCreator().getId().equals(sessionUtils.getSessionUser().getId())) {
                throw new ForbiddenException("You need to be the creator of a Competition to set a picture");
            }

            Path competitionFolder = Paths.get(competitionPicturesFolder);
            if (!Files.exists(competitionFolder)) {
                try {
                    Files.createDirectories(competitionFolder);
                } catch (IOException e) {
                    throw new FileInputException(e);
                }
            }

            String filePath = competitionFolder.toString() + "/" + toSaveFor.getId() + "." + extension;

            try (InputStream inputStream = multipartFile.getInputStream()) {
                if (toSaveFor.getPicturePath() != null) {
                    if (Files.exists(Paths.get(toSaveFor.getPicturePath()))) {
                        Files.delete(Paths.get(toSaveFor.getPicturePath()));
                    }
                }
                writeImage(filePath, inputStream);
                toSaveFor.setPicturePath(filePath);
                competitionRepository.save(toSaveFor);
                return filePath;
            } catch (IOException ioException) {
                throw new FileInputException("Could not save competition image file: " + multipartFile.getOriginalFilename(), ioException);
            }
        } else {
            throw new FileInputException("Wrong file format");
        }
    }


    private void writeImage(String pathToFile, InputStream inputStream) throws IOException {
        LOGGER.debug("writeImage {}", pathToFile);
        Path filePath = Paths.get(pathToFile);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
