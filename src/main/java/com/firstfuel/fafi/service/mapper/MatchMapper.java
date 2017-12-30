package com.firstfuel.fafi.service.mapper;

import com.firstfuel.fafi.domain.*;
import com.firstfuel.fafi.service.dto.MatchDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Match and its DTO MatchDTO.
 */
@Mapper(componentModel = "spring", uses = {TournamentMapper.class, FranchiseMapper.class})
public interface MatchMapper extends EntityMapper<MatchDTO, Match> {

    @Mapping(source = "tournament.id", target = "tournamentId")
    @Mapping(source = "tournament.name", target = "tournamentName")
    @Mapping(source = "franchise1.id", target = "franchise1Id")
    @Mapping(source = "franchise1.name", target = "franchise1Name")
    @Mapping(source = "franchise2.id", target = "franchise2Id")
    @Mapping(source = "franchise2.name", target = "franchise2Name")
    @Mapping(source = "winner.id", target = "winnerId")
    @Mapping(source = "winner.name", target = "winnerName")
    MatchDTO toDto(Match match);

    @Mapping(source = "tournamentId", target = "tournament")
    @Mapping(source = "franchise1Id", target = "franchise1")
    @Mapping(source = "franchise2Id", target = "franchise2")
    @Mapping(source = "winnerId", target = "winner")
    Match toEntity(MatchDTO matchDTO);

    default Match fromId(Long id) {
        if (id == null) {
            return null;
        }
        Match match = new Match();
        match.setId(id);
        return match;
    }
}
