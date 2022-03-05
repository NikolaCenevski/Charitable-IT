package it.finki.charitable.entities;

import javax.persistence.*;

@Entity
@Table(name = "emailMessage")
public class EmailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sendTo;
    private String subject;
    private String text;

    public EmailMessage() {
    }

    public EmailMessage(String sendTo, String subject, String text) {
        this.sendTo = sendTo;
        this.subject = subject;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String to) {
        this.sendTo = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
