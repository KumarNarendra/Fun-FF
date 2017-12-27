package com.firstfuel.fafi.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.firstfuel.fafi.domain.enumeration.Games;

/**
 * A DTO for the Player entity.
 */
public class PlayerDTO
    implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Double basePrice;

    private Double bidPrice;

    private Set<Games> optedGames;

    private Double points;

    private Long franchiseId;

    private String franchiseName;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice( Double basePrice ) {
        this.basePrice = basePrice;
    }

    public Double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice( Double bidPrice ) {
        this.bidPrice = bidPrice;
    }

    public Set<Games> getOptedGames() {
        return optedGames;
    }

    public void setOptedGames( Set<Games> optedGames ) {
        this.optedGames = optedGames;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public Long getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId( Long franchiseId ) {
        this.franchiseId = franchiseId;
    }

    public String getFranchiseName() {
        return franchiseName;
    }

    public void setFranchiseName( String franchiseName ) {
        this.franchiseName = franchiseName;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PlayerDTO playerDTO = (PlayerDTO)o;
        if ( playerDTO.getId() == null || getId() == null ) {
            return false;
        }
        return Objects.equals( getId(), playerDTO.getId() );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( getId() );
    }

    @Override
    public String toString() {
        return "PlayerDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", basePrice=" + getBasePrice() +
            ", bidPrice=" + getBidPrice() +
            ", optedGames='" + getOptedGames() + "'" +
            ", points=" + getPoints() +
            "}";
    }
}
