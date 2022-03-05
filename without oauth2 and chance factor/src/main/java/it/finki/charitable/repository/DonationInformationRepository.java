package it.finki.charitable.repository;

import it.finki.charitable.entities.DonationInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationInformationRepository extends JpaRepository<DonationInformation, Long> {
}
