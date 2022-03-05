package it.finki.charitable.services;

import it.finki.charitable.entities.EmailMessage;
import it.finki.charitable.repository.EmailMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailMessageService {

    private final EmailMessageRepository emailMessageRepository;

    public EmailMessageService(EmailMessageRepository emailMessageRepository) {
        this.emailMessageRepository = emailMessageRepository;
    }

    public List<EmailMessage> findAll() {
        return emailMessageRepository.findAll();
    }

    public void save(EmailMessage message) {
        emailMessageRepository.save(message);
    }

    public void delete(EmailMessage message) {
        emailMessageRepository.delete(message);
    }
}
