package it.finki.charitable.repository;

import it.finki.charitable.entities.AppUser;
import it.finki.charitable.entities.DonationPost;
import it.finki.charitable.entities.Moderator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationPostRepository extends JpaRepository<DonationPost, Long> {
    List<DonationPost> findAllByUser(AppUser user);
    List<DonationPost> findAllByApproved(Boolean approved);
    Page<DonationPost> findAllByModerator(Pageable pageable, Moderator moderator);
    Page<DonationPost> findAllByApproved(Pageable pageable, boolean approved);
}
