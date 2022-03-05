package it.finki.charitable.entities;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MainUser extends AppUser {
    private String creditCardInfo;

    @OneToMany
    private List<DonationInformation> donationInformation = new ArrayList<>();

    public String getCreditCardInfo() {
        return creditCardInfo;
    }

    public void setCreditCardInfo(String creditCardInfo) {
        this.creditCardInfo = creditCardInfo;
    }

    public List<DonationInformation> getDonationInformation() {
        return donationInformation;
    }

    public void setDonationInformation(List<DonationInformation> donationInformation) {
        this.donationInformation = donationInformation;
    }
}
