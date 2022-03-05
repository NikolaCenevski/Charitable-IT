package it.finki.charitable.entities;

import javax.persistence.*;

@Entity
@Table(name = "donation_information")
public class DonationInformation {

    @SequenceGenerator(
            name = "donation_information_sequence",
            sequenceName = "donation_information_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "donation_information_sequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    private float donatedAmount;
    private Long postId;
    private String title;

    public DonationInformation() {
    }

    public DonationInformation(float donatedAmount, Long postId, String title) {
        this.donatedAmount = donatedAmount;
        this.postId = postId;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getDonatedAmount() {
        return donatedAmount;
    }

    public void setDonatedAmount(float donatedAmount) {
        this.donatedAmount = donatedAmount;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
