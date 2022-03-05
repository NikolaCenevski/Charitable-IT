package it.finki.charitable.services;

import it.finki.charitable.entities.DonationInformation;
import it.finki.charitable.repository.DonationInformationRepository;
import org.springframework.stereotype.Service;

@Service
public class DonationInformationService {

    private final DonationInformationRepository donationInformationRepository;

    public DonationInformationService(DonationInformationRepository donationInformationRepository) {
        this.donationInformationRepository = donationInformationRepository;
    }

    public void save(DonationInformation donationInformation) {
        donationInformationRepository.save(donationInformation);
    }
}

