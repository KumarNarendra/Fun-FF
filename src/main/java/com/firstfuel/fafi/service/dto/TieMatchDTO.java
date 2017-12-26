package com.firstfuel.fafi.service.dto;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the TieMatch entity.
 */
public class TieMatchDTO implements Serializable {

    private Long id;

    private Double pointsForTieTeam1;

    private Double pointsForTieTeam2;

    private Long matchId;

    private Long team1Id;

    private Long team2Id;

    private Long winnerId;

    private String team1Name;

    private String team2Name;

    private String winnerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPointsForTieTeam1() {
        return pointsForTieTeam1;
    }

    public void setPointsForTieTeam1(Double pointsForTieTeam1) {
        this.pointsForTieTeam1 = pointsForTieTeam1;
    }

    public Double getPointsForTieTeam2() {
        return pointsForTieTeam2;
    }

    public void setPointsForTieTeam2(Double pointsForTieTeam2) {
        this.pointsForTieTeam2 = pointsForTieTeam2;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(Long tieTeamId) {
        this.team1Id = tieTeamId;
    }

    public Long getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(Long tieTeamId) {
        this.team2Id = tieTeamId;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId( Long winnerId ) {
        this.winnerId = winnerId;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public void setTeam1Name( String team1Name ) {
        this.team1Name = team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public void setTeam2Name( String team2Name ) {
        this.team2Name = team2Name;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName( String winnerName ) {
        this.winnerName = winnerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TieMatchDTO tieMatchDTO = (TieMatchDTO) o;
        if(tieMatchDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tieMatchDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TieMatchDTO{" +
            "id=" + getId() +
            ", pointsForTieTeam1=" + getPointsForTieTeam1() +
            ", pointsForTieTeam2=" + getPointsForTieTeam2() +
            "}";
    }
}
