package it.finki.charitable.repository;

import it.finki.charitable.entities.DonationPost;
import it.finki.charitable.entities.ReportPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {

    ReportPost findByDonationPost(DonationPost post);
}
