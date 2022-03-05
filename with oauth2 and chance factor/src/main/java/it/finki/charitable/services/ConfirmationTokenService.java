package it.finki.charitable.services;

import it.finki.charitable.entities.AppUser;
import it.finki.charitable.repository.ConfirmationTokenRepository;
import it.finki.charitable.security.ConfirmationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository tokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public ConfirmationToken getConfirmationToken(String token) {
        return tokenRepository.findByToken(token).orElse(null);
    }

    public List<ConfirmationToken> getAll() {
        return tokenRepository.findAll();
    }

    public void saveConfirmationToken(ConfirmationToken token) {
        tokenRepository.save(token);
    }

    public void deleteConfirmationToken(ConfirmationToken token) {
        tokenRepository.delete(token);
    }
}
