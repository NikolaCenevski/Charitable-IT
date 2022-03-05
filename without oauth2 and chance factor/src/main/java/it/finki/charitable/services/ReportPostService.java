package it.finki.charitable.services;

import it.finki.charitable.entities.DonationPost;
import it.finki.charitable.entities.Reason;
import it.finki.charitable.entities.ReportPost;
import it.finki.charitable.repository.ReportPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportPostService {

    private final ReportPostRepository reportPostRepository;

    public ReportPostService(ReportPostRepository reportPostRepository) {
        this.reportPostRepository = reportPostRepository;
    }

    public Page<ReportPost> findAll(int pageNo, int pageSize, String sort, String order) {
        Sort s = Sort.by(sort);
        s = order.equals("asc") ? s.ascending() : s.descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, s);
        return reportPostRepository.findAll(pageable);
    }

    public ReportPost findById(Long id) {
        return reportPostRepository.getById(id);
    }

    public ReportPost findByDonationPost(DonationPost post) {
        return reportPostRepository.findByDonationPost(post);
    }

    public void save(ReportPost post) {
        reportPostRepository.save(post);
    }

    public void delete(ReportPost reportPost) {
        reportPostRepository.delete(reportPost);
    }
}
