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
 * Criteria class for the Franchise entity. This class is used in FranchiseResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /franchises?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FranchiseCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private StringFilter logoPath;

    private DoubleFilter points;

    private LongFilter playersId;

    private LongFilter seasonId;

    private LongFilter ownerId;

    private LongFilter iconPlayerId;

    public FranchiseCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(StringFilter logoPath) {
        this.logoPath = logoPath;
    }

    public DoubleFilter getPoints() {
        return points;
    }

    public void setPoints(DoubleFilter points) {
        this.points = points;
    }

    public LongFilter getPlayersId() {
        return playersId;
    }

    public void setPlayersId(LongFilter playersId) {
        this.playersId = playersId;
    }

    public LongFilter getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(LongFilter seasonId) {
        this.seasonId = seasonId;
    }

    public LongFilter getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(LongFilter ownerId) {
        this.ownerId = ownerId;
    }

    public LongFilter getIconPlayerId() {
        return iconPlayerId;
    }

    public void setIconPlayerId(LongFilter iconPlayerId) {
        this.iconPlayerId = iconPlayerId;
    }

    @Override
    public String toString() {
        return "FranchiseCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (logoPath != null ? "logoPath=" + logoPath + ", " : "") +
                (points != null ? "points=" + points + ", " : "") +
                (playersId != null ? "playersId=" + playersId + ", " : "") +
                (seasonId != null ? "seasonId=" + seasonId + ", " : "") +
                (ownerId != null ? "ownerId=" + ownerId + ", " : "") +
                (iconPlayerId != null ? "iconPlayerId=" + iconPlayerId + ", " : "") +
            "}";
    }

}
