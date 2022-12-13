package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import ch.qos.logback.classic.Logger;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value = "/api/v1/forgot")
public class ForgotEndpoint {

    private final UserService userService;

    private final JavaMailSender javaMailSender;

    @Autowired
    public ForgotEndpoint(UserService userService, JavaMailSender javaMailSender) {
        this.userService = userService;
        this.javaMailSender = javaMailSender;
    }

    @PermitAll
    @CrossOrigin
    @PostMapping
    public String requestResetPassword(@RequestBody UserPasswordResetRequestDto userPasswordResetRequestDto) {
        try {
            String token = RandomString.make(32);
            System.out.println(userPasswordResetRequestDto.getEmail() + "###");
            userService.updateResetPasswordToken(userPasswordResetRequestDto.getEmail(), token);
            String resetLink = "http://localhost:4200/#/reset?token=" + token;
            sendPasswordResetMail(userPasswordResetRequestDto.getEmail(), resetLink);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No entry found for given email: " + userPasswordResetRequestDto.getEmail());
        } catch (MessagingException e) {
            //TODO add Exception Handling
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported encoding for Payload");
        }
        return "{\"success\": \"true\"}";
    }

    private void sendPasswordResetMail(String recipient, String resetLink) throws MessagingException, UnsupportedEncodingException {
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
