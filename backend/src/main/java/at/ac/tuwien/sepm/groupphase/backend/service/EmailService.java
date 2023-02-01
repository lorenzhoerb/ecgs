package at.ac.tuwien.sepm.groupphase.backend.service;

import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
public interface EmailService {
    /**
     * Sends an email to "to" with the given message.
     *
     * @param to      recipient email
     * @param message message to send
     * @throws MessagingException           if the message could not be sent
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    void sendEmail(String to, String message) throws MessagingException, UnsupportedEncodingException;

    /**
     * Sends an email with a password reset link.
     *
     * @param recipient the recipient of the message
     * @param resetLink the link for the password reset
     * @throws MessagingException           if the message could not be sent
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    void sendPasswordResetMail(String recipient, String resetLink) throws MessagingException, UnsupportedEncodingException;
}
