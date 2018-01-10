package com.firstfuel.fafi.service.mapper;

import com.firstfuel.fafi.domain.*;
import com.firstfuel.fafi.service.dto.FranchiseDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Franchise and its DTO FranchiseDTO.
 */
@Mapper(componentModel = "spring", uses = {SeasonMapper.class, PlayerMapper.class})
public interface FranchiseMapper extends EntityMapper<FranchiseDTO, Franchise> {

    @Mapping(source = "season.id", target = "seasonId")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "iconPlayer.id", target = "iconPlayerId")
    @Mapping(source = "season.name", target = "seasonName")
    @Mapping(source = "owner.name", target = "ownerName")
    @Mapping(source = "iconPlayer.name", target = "iconPlayerName")
    @Mapping(source = "points", target = "points", defaultValue = "0.0d")
    FranchiseDTO toDto( Franchise franchise );

    @Mapping(target = "players", ignore = true)
    @Mapping(source = "seasonId", target = "season")
    @Mapping(source = "ownerId", target = "owner")
    @Mapping(source = "iconPlayerId", target = "iconPlayer")
    Franchise toEntity(FranchiseDTO franchiseDTO);

    default Franchise fromId(Long id) {
        if (id == null) {
            return null;
        }
        Franchise franchise = new Franchise();
        franchise.setId(id);
        return franchise;
    }
}
