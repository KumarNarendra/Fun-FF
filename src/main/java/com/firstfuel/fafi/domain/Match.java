package com.firstfuel.fafi.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Match.
 */
@Entity
@Table(name = "fafi_match")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Match implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date_time")
    private ZonedDateTime startDateTime;

    @Column(name = "end_date_time")
    private ZonedDateTime endDateTime;

    @Column(name = "points_for_franchise_1")
    private Double pointsForFranchise1;

    @Column(name = "points_for_franchise_2")
    private Double pointsForFranchise2;

    @ManyToOne
    private Tournament tournament;

    @ManyToOne
    private Franchise franchise1;

    @ManyToOne
    private Franchise franchise2;

    @ManyToOne
    private Franchise winner;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public Match startDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public Match endDateTime(ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

    public void setEndDateTime(ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Double getPointsForFranchise1() {
        return pointsForFranchise1;
    }

    public Match pointsForFranchise1(Double pointsForFranchise1) {
        this.pointsForFranchise1 = pointsForFranchise1;
        return this;
    }

    public void setPointsForFranchise1(Double pointsForFranchise1) {
        this.pointsForFranchise1 = pointsForFranchise1;
    }

    public Double getPointsForFranchise2() {
        return pointsForFranchise2;
    }

    public Match pointsForFranchise2(Double pointsForFranchise2) {
        this.pointsForFranchise2 = pointsForFranchise2;
        return this;
    }

    public void setPointsForFranchise2(Double pointsForFranchise2) {
        this.pointsForFranchise2 = pointsForFranchise2;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Match tournament(Tournament tournament) {
        this.tournament = tournament;
        return this;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Franchise getFranchise1() {
        return franchise1;
    }

    public Match franchise1(Franchise franchise) {
        this.franchise1 = franchise;
        return this;
    }

    public void setFranchise1(Franchise franchise) {
        this.franchise1 = franchise;
    }

    public Franchise getFranchise2() {
        return franchise2;
    }

    public Match franchise2(Franchise franchise) {
        this.franchise2 = franchise;
        return this;
    }

    public void setFranchise2(Franchise franchise) {
        this.franchise2 = franchise;
    }

    public Franchise getWinner() {
        return winner;
    }

    public Match winner(Franchise franchise) {
        this.winner = franchise;
        return this;
    }

    public void setWinner(Franchise franchise) {
        this.winner = franchise;
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
        Match match = (Match) o;
        if (match.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), match.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Match{" +
            "id=" + getId() +
            ", startDateTime='" + getStartDateTime() + "'" +
            ", endDateTime='" + getEndDateTime() + "'" +
            ", pointsForFranchise1=" + getPointsForFranchise1() +
            ", pointsForFranchise2=" + getPointsForFranchise2() +
            "}";
    }
}
