package it.finki.charitable.repository;

import it.finki.charitable.entities.FundsCollected;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundsCollectedRepository extends JpaRepository<FundsCollected, Long> {
}
