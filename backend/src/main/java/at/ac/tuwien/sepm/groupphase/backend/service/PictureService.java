package at.ac.tuwien.sepm.groupphase.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PictureService {

    /**
     * This method saves a picture for a user.
     *
     * @param multipartFile the fileContent to upload
     * @return the path where the user image has been stored
     */
    String saveUserPicture(MultipartFile multipartFile);

    /**
     * This method saves a picture for a competition.
     *
     * @param id the id of the competition to set the picture for
     * @param multipartFile the fileContent to upload
     * @return the path where the competition image has been stored
     */
    String saveCompetitionImage(Long id, MultipartFile multipartFile);
}
