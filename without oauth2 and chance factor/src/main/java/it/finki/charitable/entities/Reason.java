package it.finki.charitable.entities;

import javax.persistence.*;

@Entity
@Table(name = "reason")
public class Reason {

    @SequenceGenerator(
            name = "reason_sequence",
            sequenceName = "reason_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reason_sequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    private String description;
    @OneToOne
    private AppUser user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
}
