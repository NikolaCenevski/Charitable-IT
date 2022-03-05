package it.finki.charitable.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donation_post")
public class DonationPost {

    @SequenceGenerator(
            name = "donation_post_sequence",
            sequenceName = "donation_post_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "donation_post_sequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    private String title;
    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;
    private float fundsNeeded;
    private String currency;
    private LocalDate dateDue;
    private String bankAccount;
    private Boolean approved;

    @ElementCollection
    List<String> phoneNumbers;

    @ElementCollection
    List<String> images;

    @ElementCollection
    List<String> moderatorImages;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "app_user_id"
    )
    private AppUser user;

    @ManyToOne
    private Moderator moderator;

    @OneToMany
    private List<FundsCollected> fundsCollected = new ArrayList<>();

    @Transient
    public List<String> getImagesPath() {
        if (images == null || id == null) return null;

        List<String> photoPaths = new ArrayList<>();
        for(String path: images) {
            photoPaths.add("../../../../post-photos/" + id + "/" + path);
        }

        return photoPaths;
    }

    @Transient
    public List<String> getModeratorPath() {
        if (images == null || id == null) return null;

        List<String> photoPaths = new ArrayList<>();

        for(String path: images) {
            photoPaths.add("../../../../post-photos/" + id + "/" + path);
        }

        for(String path: moderatorImages) {
            photoPaths.add("../../../../moderator-photos/" + id + "/" + path);
        }

        return photoPaths;
    }

    @Transient
    public List<String> getPhotosForDeletion() {
        if (images == null || id == null) return null;

        List<String> photoPaths = new ArrayList<>();
        for(String path: images) {
            photoPaths.add("post-photos\\" + id + "\\" + path);
        }
        photoPaths.add("post-photos\\" + id);

        for(String path: moderatorImages) {
            photoPaths.add("moderator-photos\\" + id + "\\" + path);
        }
        photoPaths.add("moderator-photos\\" + id);

        return photoPaths;
    }

    public DonationPost() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getFundsNeeded() {
        return fundsNeeded;
    }

    public void setFundsNeeded(float fundsNeeded) {
        this.fundsNeeded = fundsNeeded;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDateDue() {
        return dateDue;
    }

    public void setDateDue(LocalDate dateDue) {
        this.dateDue = dateDue;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getModeratorImages() {
        return moderatorImages;
    }

    public void setModeratorImages(List<String> moderatorImages) {
        this.moderatorImages = moderatorImages;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Moderator getModerator() {
        return moderator;
    }

    public void setModerator(Moderator moderator) {
        this.moderator = moderator;
    }

    public List<FundsCollected> getFundsCollected() {
        return fundsCollected;
    }

    public void setFundsCollected(List<FundsCollected> fundsCollected) {
        this.fundsCollected = fundsCollected;
    }
}
