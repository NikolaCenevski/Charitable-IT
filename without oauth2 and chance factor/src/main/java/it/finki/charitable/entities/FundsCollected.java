package it.finki.charitable.entities;

import javax.persistence.*;

@Entity
@Table(name = "funds_collected")
public class FundsCollected {

    @SequenceGenerator(
            name = "funds_collected_sequence",
            sequenceName = "funds_collected_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "funds_collected_sequence"
    )
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    private String description;

    private float funds;

    public FundsCollected() {
    }

    public FundsCollected(String description, float funds) {
        this.description = description;
        this.funds = funds;
    }

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

    public float getFunds() {
        return funds;
    }

    public void setFunds(float funds) {
        this.funds = funds;
    }
}
