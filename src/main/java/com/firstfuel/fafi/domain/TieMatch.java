package com.firstfuel.fafi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A TieMatch.
 */
@Entity
@Table(name = "tie_match")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TieMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "points_for_tie_team_1")
    private Double pointsForTieTeam1;

    @Column(name = "points_for_tie_team_2")
    private Double pointsForTieTeam2;

    @ManyToOne
    private Match match;

    @OneToOne
    @JoinColumn(unique = true)
    private TieTeam team1;

    @OneToOne
    @JoinColumn(unique = true)
    private TieTeam team2;

    @OneToOne
    @JsonIgnore
    private TieTeam winner;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPointsForTieTeam1() {
        return pointsForTieTeam1;
    }

    public TieMatch pointsForTieTeam1(Double pointsForTieTeam1) {
        this.pointsForTieTeam1 = pointsForTieTeam1;
        return this;
    }

    public void setPointsForTieTeam1(Double pointsForTieTeam1) {
        this.pointsForTieTeam1 = pointsForTieTeam1;
    }

    public Double getPointsForTieTeam2() {
        return pointsForTieTeam2;
    }

    public TieMatch pointsForTieTeam2(Double pointsForTieTeam2) {
        this.pointsForTieTeam2 = pointsForTieTeam2;
        return this;
    }

    public void setPointsForTieTeam2(Double pointsForTieTeam2) {
        this.pointsForTieTeam2 = pointsForTieTeam2;
    }

    public Match getMatch() {
        return match;
    }

    public TieMatch match(Match match) {
        this.match = match;
        return this;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public TieTeam getTeam1() {
        return team1;
    }

    public TieMatch team1(TieTeam tieTeam) {
        this.team1 = tieTeam;
        return this;
    }

    public void setTeam1(TieTeam tieTeam) {
        this.team1 = tieTeam;
    }

    public TieTeam getTeam2() {
        return team2;
    }

    public TieMatch team2(TieTeam tieTeam) {
        this.team2 = tieTeam;
        return this;
    }

    public void setTeam2(TieTeam tieTeam) {
        this.team2 = tieTeam;
    }

    public TieTeam getWinner() {
        return winner;
    }

    public TieMatch winner(TieTeam tieTeam) {
        this.winner = tieTeam;
        return this;
    }

    public void setWinner(TieTeam tieTeam) {
        this.winner = tieTeam;
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
        TieMatch tieMatch = (TieMatch) o;
        if (tieMatch.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tieMatch.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TieMatch{" +
            "id=" + getId() +
            ", pointsForTieTeam1=" + getPointsForTieTeam1() +
            ", pointsForTieTeam2=" + getPointsForTieTeam2() +
            "}";
    }
}
