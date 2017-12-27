package com.firstfuel.fafi.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import com.firstfuel.fafi.domain.enumeration.Games;

/**
 * A Player.
 */
@Entity
@Table(name = "player")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_price")
    private Double basePrice;

    @Column(name = "bid_price")
    private Double bidPrice;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "opted_games")
    private Set<Games> optedGames;

    @Column(name = "points")
    private Double points;

    @ManyToOne
    private Franchise franchise;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Player name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public Player basePrice(Double basePrice) {
        this.basePrice = basePrice;
        return this;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getBidPrice() {
        return bidPrice;
    }

    public Player bidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
        return this;
    }

    public void setBidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Set<Games> getOptedGames() {
        return optedGames;
    }

    public Player optedGames(Set<Games> optedGames) {
        this.optedGames = optedGames;
        return this;
    }

    public void setOptedGames(Set<Games> optedGames) {
        this.optedGames = optedGames;
    }

    public Double getPoints() {
        return points;
    }

    public Player points(Double points) {
        this.points = points;
        return this;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public Franchise getFranchise() {
        return franchise;
    }

    public Player franchise(Franchise franchise) {
        this.franchise = franchise;
        return this;
    }

    public void setFranchise(Franchise franchise) {
        this.franchise = franchise;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        if (player.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), player.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Player{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", basePrice=" + getBasePrice() +
            ", bidPrice=" + getBidPrice() +
            ", optedGames='" + getOptedGames() + "'" +
            ", points=" + getPoints() +
            "}";
    }
}
