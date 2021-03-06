package com.firstfuel.fafi.service.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * </p>
 *
 * @author Narendra Kumar
 * @version 1.0
 * @since 27-Dec-2017
 */
public class PlayerStandingsDTO {
    private Integer rank;
    private FranchiseDTO franchise;
    private PlayerDTO player;
    private Integer totalMatchesPlayed;
    private Double totalPoints;
    private List<Boolean> currentForm;
    private List<MatchWiseDetails> matchWiseDetails;

    public PlayerStandingsDTO() {
        this.totalMatchesPlayed = 0;
        this.totalPoints = 0d;
        this.matchWiseDetails = new ArrayList<>();
    }

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

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer( PlayerDTO player ) {
        this.player = player;
    }

    public Integer getTotalMatchesPlayed() {
        return totalMatchesPlayed;
    }

    public void setTotalMatchesPlayed( Integer totalMatchesPlayed ) {
        this.totalMatchesPlayed = totalMatchesPlayed;
    }

    public Double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints( Double totalPoints ) {
        this.totalPoints = totalPoints;
    }

    public List<Boolean> getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm( List<Boolean> currentForm ) {
        this.currentForm = currentForm;
    }

    public List<MatchWiseDetails> getMatchWiseDetails() {
        return matchWiseDetails;
    }

    public void setMatchWiseDetails( List<MatchWiseDetails> matchWiseDetails ) {
        this.matchWiseDetails = matchWiseDetails;
    }

    public void addMatchWiseDetails( Long matchId, boolean matchResult, Double matchPoints ) {
        this.matchWiseDetails.add( new MatchWiseDetails( matchId, matchResult, matchPoints ) );
    }

    @Override
    public String toString() {
        return "PlayerStandingsDTO{" +
            "rank=" + rank +
            ", franchise=" + franchise +
            ", player=" + player +
            ", totalMatchesPlayed=" + totalMatchesPlayed +
            ", totalPoints=" + totalPoints +
            ", currentForm=" + currentForm +
            ", matchWiseDetails=" + matchWiseDetails +
            '}';
    }

    class MatchWiseDetails {
        private Long matchId;
        private boolean matchResult;
        private Double matchPoints;

        public MatchWiseDetails( Long matchId, boolean matchResult, Double matchPoints ) {
            this.matchId = matchId;
            this.matchResult = matchResult;
            this.matchPoints = matchPoints;
        }

        public Long getMatchId() {
            return matchId;
        }

        public void setMatchId( Long matchId ) {
            this.matchId = matchId;
        }

        public boolean isMatchResult() {
            return matchResult;
        }

        public void setMatchResult( boolean matchResult ) {
            this.matchResult = matchResult;
        }

        public Double getMatchPoints() {
            return matchPoints;
        }

        public void setMatchPoints( Double matchPoints ) {
            this.matchPoints = matchPoints;
        }
    }
}
