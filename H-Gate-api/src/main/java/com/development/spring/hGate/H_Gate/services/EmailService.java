package com.development.spring.hGate.H_Gate.services;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserService userService;

    private final String allowedOrigins;

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(@Value("${application.cors.allowedOrigins}") String allowedOrigins,
                        @Lazy UserService userService, JavaMailSender javaMailSender) {
        super();
        this.userService = userService;
        this.javaMailSender = javaMailSender;
        this.allowedOrigins = allowedOrigins;
    }

    @Async
    public void inviaEmail(String email, String titolo, String messaggio) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(titolo);
            helper.setText(messaggio, true);

            javaMailSender.send(message);

            logger.info("Email sent successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


//    public void sendResetPassword(String token, Users user) {
//        try {
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(user.getEmail());
//            helper.setSubject("Reset Password");
//            String messageText = "Hello,<br><br>"
//                    + "Your temporary token is: " + "<br>"
//                    + token + "<br>"
//                    + "You can use this token to change your password.<br><br>"
//                    + "The token will expire in 30 minutes.<br><br>"
//                    + "You can log in <a href='"+ allowedOrigins +"/'>here</a>.<br><br>"
//                    + "Thank you!";
//            helper.setText(messageText, true);
//
//            javaMailSender.send(message);
//
//            System.out.println("Email sent successfully");
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }

//    @Async
//    public void notifyUnauthorizedAccess(User userAttemptedAccess) {
//
//        Set<User> higherAuthorityUsers  = userService.getHigherAuthorityUsers();
//
//        try {
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setSubject("UNAUTHORIZED ACCESS");
//            String messageText = "Warning!!<br><br>"
//                    + "We have detected multiple unauthorized access attempts to Argo using this email address:" +
//                    " <strong>" + userAttemptedAccess.getEmail() + "</strong>.<br><br>";
//            helper.setText(messageText, true);
//
//            for(User user : higherAuthorityUsers){
//                helper.setTo(user.getEmail());
//                javaMailSender.send(message);
//            }
//
//            String msg = "Unauthorized access notified to higher authority users";
//            logger.debug(msg);
//
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
//
//    }


//    public void sendTempPasswordEmail(String email, String password) {
//        try {
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setTo(email);
//            helper.setSubject("Temporary Password");
//            String messageText = "Hello,<br><br>"
//                    + "Your temporary password is: " + "<br>"
//                    + password + "<br>"
//                    + "Please log in using this password and change it immediately.<br><br>"
//                    + "You can log in <a href='"+ allowedOrigins +"/'>here</a>.<br><br>"
//                    + "Thank you!";
//
//            helper.setText(messageText, true);
//
//            javaMailSender.send(message);
//
//            System.out.println("Email sent successfully to " + email);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
}
