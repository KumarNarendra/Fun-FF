package com.firstfuel.fafi.service.dto;


import java.time.ZonedDateTime;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Match entity.
 */
public class MatchDTO implements Serializable {

    private Long id;

    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

    private Double pointsForFranchise1;

    private Double pointsForFranchise2;

    private Long tournamentId;

    private String tournamentName;

    private Long franchise1Id;

    private String franchise1Name;

    private Long franchise2Id;

    private String franchise2Name;

    private Long winnerId;

    private String winnerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Double getPointsForFranchise1() {
        return pointsForFranchise1;
    }

    public void setPointsForFranchise1(Double pointsForFranchise1) {
        this.pointsForFranchise1 = pointsForFranchise1;
    }

    public Double getPointsForFranchise2() {
        return pointsForFranchise2;
    }

    public void setPointsForFranchise2(Double pointsForFranchise2) {
        this.pointsForFranchise2 = pointsForFranchise2;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName( String tournamentName ) {
        this.tournamentName = tournamentName;
    }

    public Long getFranchise1Id() {
        return franchise1Id;
    }

    public void setFranchise1Id(Long franchiseId) {
        this.franchise1Id = franchiseId;
    }

    public String getFranchise1Name() {
        return franchise1Name;
    }

    public void setFranchise1Name(String franchiseName) {
        this.franchise1Name = franchiseName;
    }

    public Long getFranchise2Id() {
        return franchise2Id;
    }

    public void setFranchise2Id(Long franchiseId) {
        this.franchise2Id = franchiseId;
    }

    public String getFranchise2Name() {
        return franchise2Name;
    }

    public void setFranchise2Name(String franchiseName) {
        this.franchise2Name = franchiseName;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long franchiseId) {
        this.winnerId = franchiseId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String franchiseName) {
        this.winnerName = franchiseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MatchDTO matchDTO = (MatchDTO) o;
        if(matchDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), matchDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MatchDTO{" +
            "id=" + getId() +
            ", startDateTime='" + getStartDateTime() + "'" +
            ", endDateTime='" + getEndDateTime() + "'" +
            ", pointsForFranchise1=" + getPointsForFranchise1() +
            ", pointsForFranchise2=" + getPointsForFranchise2() +
            "}";
    }
}
