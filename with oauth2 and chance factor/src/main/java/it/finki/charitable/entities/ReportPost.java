package it.finki.charitable.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report_post")
public class ReportPost {

    @SequenceGenerator(
            name = "report_post_sequence",
            sequenceName = "report_post_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "report_post_sequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @OneToOne
    private DonationPost donationPost;

    @OneToMany
    private List<Reason> reasons = new ArrayList<>();

    private int numReports = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DonationPost getDonationPost() {
        return donationPost;
    }

    public void setDonationPost(DonationPost donationPost) {
        this.donationPost = donationPost;
    }

    public List<Reason> getReasons() {
        return reasons;
    }

    public void setReasons(List<Reason> reasons) {
        this.reasons = reasons;
    }

    public int getNumReports() {
        return numReports;
    }

    public void setNumReports(int numReports) {
        this.numReports = numReports;
    }
}
