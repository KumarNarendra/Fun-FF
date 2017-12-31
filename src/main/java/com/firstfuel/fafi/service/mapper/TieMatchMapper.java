package com.firstfuel.fafi.service.mapper;

import com.firstfuel.fafi.domain.*;
import com.firstfuel.fafi.service.dto.TieMatchDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity TieMatch and its DTO TieMatchDTO.
 */
@Mapper(componentModel = "spring", uses = {MatchMapper.class, TieTeamMapper.class})
public interface TieMatchMapper extends EntityMapper<TieMatchDTO, TieMatch> {

    @Mapping(source = "match.id", target = "matchId")
    @Mapping(source = "team1.id", target = "team1Id")
    @Mapping(source = "team1.name", target = "team1Name")
    @Mapping(source = "team2.id", target = "team2Id")
    @Mapping(source = "team2.name", target = "team2Name")
    @Mapping(source = "winner.id", target = "winnerId")
    @Mapping(source = "winner.name", target = "winnerName")
    TieMatchDTO toDto(TieMatch tieMatch); 

    @Mapping(source = "matchId", target = "match")
    @Mapping(source = "team1Id", target = "team1")
    @Mapping(source = "team2Id", target = "team2")
    @Mapping(source = "winnerId", target = "winner")
    TieMatch toEntity(TieMatchDTO tieMatchDTO);

    default TieMatch fromId(Long id) {
        if (id == null) {
            return null;
        }
        TieMatch tieMatch = new TieMatch();
        tieMatch.setId(id);
        return tieMatch;
    }
}
