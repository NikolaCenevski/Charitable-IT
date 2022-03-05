package it.finki.charitable.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendValidationEmail(String to, String subject, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);

        String text = "Verify your account on the following link\n" +
                "http://localhost:9091/validate?token=" + token;
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendApprovalEmail(String to, String subject, Long postId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);

        String text = "Your post has been approved\n" + "http://localhost:9091/post?postid=" + postId;
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendNoApprovalEmail(String to, String subject, String description) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);

        String text = "Sorry, your post hasn't been approved\n" +
                "Moderator:\n" + description;
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendDeletionEmail(String to, String subject, String description) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);

        String text = "Sorry, your post has been deleted\n" +
                "Moderator:\n" + description;
        message.setText(text);
        javaMailSender.send(message);
    }
}
