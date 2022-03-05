package it.finki.charitable.security;

import it.finki.charitable.entities.AppUser;

import javax.persistence.*;

@Entity
@Table(name = "confirmation_token")
public class ConfirmationToken {

    @SequenceGenerator(
            name = "conf_token_sequence",
            sequenceName = "conf_token_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "conf_token_sequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(
            nullable = false,
            name = "app_user_id"
    )
    private AppUser user;

    public ConfirmationToken() {
    }

    public ConfirmationToken(String token, AppUser user) {
        this.token = token;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
}
