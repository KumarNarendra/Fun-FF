package com.firstfuel.fafi.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the TieMatch entity. This class is used in TieMatchResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /tie-matches?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TieMatchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private DoubleFilter pointsForTieTeam1;

    private DoubleFilter pointsForTieTeam2;

    private LongFilter matchId;

    private LongFilter team1Id;

    private LongFilter team2Id;

    private LongFilter winnerId;

    public TieMatchCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public DoubleFilter getPointsForTieTeam1() {
        return pointsForTieTeam1;
    }

    public void setPointsForTieTeam1(DoubleFilter pointsForTieTeam1) {
        this.pointsForTieTeam1 = pointsForTieTeam1;
    }

    public DoubleFilter getPointsForTieTeam2() {
        return pointsForTieTeam2;
    }

    public void setPointsForTieTeam2(DoubleFilter pointsForTieTeam2) {
        this.pointsForTieTeam2 = pointsForTieTeam2;
    }

    public LongFilter getMatchId() {
        return matchId;
    }

    public void setMatchId(LongFilter matchId) {
        this.matchId = matchId;
    }

    public LongFilter getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(LongFilter team1Id) {
        this.team1Id = team1Id;
    }

    public LongFilter getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(LongFilter team2Id) {
        this.team2Id = team2Id;
    }

    public LongFilter getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(LongFilter winnerId) {
        this.winnerId = winnerId;
    }

    @Override
    public String toString() {
        return "TieMatchCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (pointsForTieTeam1 != null ? "pointsForTieTeam1=" + pointsForTieTeam1 + ", " : "") +
                (pointsForTieTeam2 != null ? "pointsForTieTeam2=" + pointsForTieTeam2 + ", " : "") +
                (matchId != null ? "matchId=" + matchId + ", " : "") +
                (team1Id != null ? "team1Id=" + team1Id + ", " : "") +
                (team2Id != null ? "team2Id=" + team2Id + ", " : "") +
                (winnerId != null ? "winnerId=" + winnerId + ", " : "") +
            "}";
    }

}
