package it.finki.charitable.services;

import it.finki.charitable.entities.EmailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailMessageService emailMessageService;

    public EmailService(JavaMailSender javaMailSender, EmailMessageService emailMessageService) {
        this.javaMailSender = javaMailSender;
        this.emailMessageService = emailMessageService;
    }

    public void sendValidationEmail(String to, String subject, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);

        String text = "Verify your account on the following link\n" +
                "http://localhost:9091/validate?token=" + token;
        message.setText(text);
        sendMail(message);
    }

    public void sendApprovalEmail(String to, String subject, Long postId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);

        String text = "Your post has been approved\n" + "http://localhost:9091/post?postid=" + postId;
        message.setText(text);
        sendMail(message);
    }

    public void sendNoApprovalEmail(String to, String subject, String description) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);

        String text = "Sorry, your post hasn't been approved\n" +
                "Moderator:\n" + description;
        message.setText(text);
        sendMail(message);
    }

    public void sendDeletionEmail(String to, String subject, String description) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);

        String text = "Sorry, your post has been deleted\n" +
                "Moderator:\n" + description;
        message.setText(text);
        sendMail(message);
    }

    @Async
    public void sendMail(SimpleMailMessage message) {
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            emailMessageService.save(new EmailMessage(message.getTo()[0], message.getSubject(), message.getText()));
        }
    }
}
