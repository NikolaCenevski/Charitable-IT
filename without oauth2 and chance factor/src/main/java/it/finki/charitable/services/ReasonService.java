package it.finki.charitable.services;

import it.finki.charitable.entities.Reason;
import it.finki.charitable.repository.ReasonRepository;
import org.springframework.stereotype.Service;

@Service
public class ReasonService {

    private final ReasonRepository reasonRepository;

    public ReasonService(ReasonRepository reasonRepository) {
        this.reasonRepository = reasonRepository;
    }

    public void save(Reason reason) {
        reasonRepository.save(reason);
    }

    public void delete(Reason reason) {
        reasonRepository.delete(reason);
    }
}
