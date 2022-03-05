package it.finki.charitable.services;

import it.finki.charitable.entities.AppUser;
import it.finki.charitable.entities.DonationPost;
import it.finki.charitable.entities.Moderator;
import it.finki.charitable.repository.DonationPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationPostService {

    private final DonationPostRepository donationPostRepository;

    public DonationPostService(DonationPostRepository donationPostRepository) {
        this.donationPostRepository = donationPostRepository;
    }

    public DonationPost save(DonationPost donationPost) {
        return donationPostRepository.save(donationPost);
    }

    public DonationPost getById(Long id) {
        if(donationPostRepository.existsById(id)) {
            return donationPostRepository.getById(id);
        }

        return null;
    }

    public List<DonationPost> findAll() {
        return donationPostRepository.findAll();
    }

    public List<DonationPost> findAllByUser(AppUser user) {
        return donationPostRepository.findAllByUser(user);
    }

    public List<DonationPost> findAllByApproved(Boolean approved) {
        return donationPostRepository.findAllByApproved(approved);
    }

    public Page<DonationPost> findAllByModerator(int pageNo, int pageSize, String sort, String order, Moderator moderator) {
        Sort s = Sort.by(sort);
        s = order.equals("asc") ? s.ascending() : s.descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, s);
        return donationPostRepository.findAllByModerator(pageable, moderator);
    }

    public void delete(DonationPost donationPost) {
        donationPostRepository.delete(donationPost);
    }

    public Page<DonationPost> findPaginated(int pageNo, int pageSize, String sort, String order, boolean approved) {
        Sort s = Sort.by(sort);
        s = order.equals("asc") ? s.ascending() : s.descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, s);
        return donationPostRepository.findAllByApproved(pageable, approved);
    }
}
