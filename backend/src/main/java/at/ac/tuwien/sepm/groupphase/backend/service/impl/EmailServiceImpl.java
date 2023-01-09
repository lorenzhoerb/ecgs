package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(String to, String message) throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper msgHelper = new MimeMessageHelper(msg);
        msgHelper.setFrom("ECGSservice@gmail.com", "ECGS");
        msgHelper.setTo(to);
        String subject = "ECGS - account creation notification";
        String content = message;
        msgHelper.setSubject(subject);
        msgHelper.setText(content, true);
        javaMailSender.send(msg);
    }

    @Override
    public void sendPasswordResetMail(String recipient, String resetLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper msgHelper = new MimeMessageHelper(msg);
        msgHelper.setFrom("ECGSservice@gmail.com", "ECGS");
        msgHelper.setTo(recipient);
        String subject = "ECGS - Password reset";
        String content = "<p>ECGS - Password reset service</p>"
            + "<p>Click the following link to change your password:</p>"
            + "<p><a href=\"" + resetLink + "\">Reset password</a></p>";
        msgHelper.setSubject(subject);
        msgHelper.setText(content, true);
        javaMailSender.send(msg);
    }
}
