package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(String to, String message) throws MessagingException, UnsupportedEncodingException {
        LOGGER.debug("sendEmail({},{})", to, message);

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
        LOGGER.debug("sendPasswordResetMail({},{})", recipient);

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
