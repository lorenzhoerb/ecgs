package at.ac.tuwien.sepm.groupphase.backend.service;

import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
public interface EmailService {
    void sendEmail(String to, String message) throws MessagingException, UnsupportedEncodingException;

    void sendPasswordResetMail(String recipient, String resetLink) throws MessagingException, UnsupportedEncodingException;
}
