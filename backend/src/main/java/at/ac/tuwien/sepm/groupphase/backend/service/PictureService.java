package at.ac.tuwien.sepm.groupphase.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PictureService {

    /**
     * This method saves a picture for a user.
     *
     * @param multipartFile the fileContent to upload
     * @return a boolean value: true when file has been stored, false if file was not stored
     */
    String saveUserPicture(MultipartFile multipartFile);

}
