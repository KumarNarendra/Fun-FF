package com.firstfuel.fafi.service.dto;

import java.io.Serializable;
import com.firstfuel.fafi.domain.enumeration.Games;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the Player entity. This class is used in PlayerResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /players?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PlayerCriteria implements Serializable {
    /**
     * Class for filtering Games
     */
    public static class GamesFilter extends Filter<Games> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private DoubleFilter basePrice;

    private DoubleFilter bidPrice;

    private GamesFilter optedGames;

    private DoubleFilter points;

    private LongFilter franchiseId;

    public PlayerCriteria() {
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

    public DoubleFilter getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(DoubleFilter basePrice) {
        this.basePrice = basePrice;
    }

    public DoubleFilter getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(DoubleFilter bidPrice) {
        this.bidPrice = bidPrice;
    }

    public GamesFilter getOptedGames() {
        return optedGames;
    }

    public void setOptedGames(GamesFilter optedGames) {
        this.optedGames = optedGames;
    }

    public DoubleFilter getPoints() {
        return points;
    }

    public void setPoints(DoubleFilter points) {
        this.points = points;
    }

    public LongFilter getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(LongFilter franchiseId) {
        this.franchiseId = franchiseId;
    }

    @Override
    public String toString() {
        return "PlayerCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (basePrice != null ? "basePrice=" + basePrice + ", " : "") +
                (bidPrice != null ? "bidPrice=" + bidPrice + ", " : "") +
                (optedGames != null ? "optedGames=" + optedGames + ", " : "") +
                (points != null ? "points=" + points + ", " : "") +
                (franchiseId != null ? "franchiseId=" + franchiseId + ", " : "") +
            "}";
    }

}
