package it.finki.charitable.services;

import it.finki.charitable.entities.FundsCollected;
import it.finki.charitable.repository.FundsCollectedRepository;
import org.springframework.stereotype.Service;

@Service
public class FundsCollectedService {

    private final FundsCollectedRepository fundsCollectedRepository;

    public FundsCollectedService(FundsCollectedRepository fundsCollectedRepository) {
        this.fundsCollectedRepository = fundsCollectedRepository;
    }

    public void save(FundsCollected fundsCollected) {
        fundsCollectedRepository.save(fundsCollected);
    }
}
