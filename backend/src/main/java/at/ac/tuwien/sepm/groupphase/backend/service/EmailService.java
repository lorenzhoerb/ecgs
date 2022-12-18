package at.ac.tuwien.sepm.groupphase.backend.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmail(String to, String message);
}
