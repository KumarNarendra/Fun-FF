package com.firstfuel.fafi.service.dto;

import java.util.List;

import com.firstfuel.fafi.domain.Franchise;

/**
 * <p>
 * </p>
 *
 * @author Narendra Kumar
 * @version 1.0
 * @since 27-Dec-2017
 */
public class FranchiseStandingsDTO {
    private Integer rank;
    private FranchiseDTO franchise;
    private Integer matchesPlayed;
    private Double points;
    private List<Boolean> currentForm;

    public Integer getRank() {
        return rank;
    }

    public void setRank( Integer rank ) {
        this.rank = rank;
    }

    public FranchiseDTO getFranchise() {
        return franchise;
    }

    public void setFranchise( FranchiseDTO franchise ) {
        this.franchise = franchise;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed( Integer matchesPlayed ) {
        this.matchesPlayed = matchesPlayed;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints( Double points ) {
        this.points = points;
    }

    public List<Boolean> getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm( List<Boolean> currentForm ) {
        this.currentForm = currentForm;
    }

    @Override
    public String toString() {
        return "FranchiseStandingsDTO{" + "rank=" + rank + ", franchise=" + franchise + ", matchesPlayed=" + matchesPlayed + ", points=" + points + ", currentForm=" + currentForm + '}';
    }
}
