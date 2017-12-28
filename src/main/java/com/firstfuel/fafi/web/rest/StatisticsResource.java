package com.firstfuel.fafi.web.rest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import com.firstfuel.fafi.service.FranchiseService;
import com.firstfuel.fafi.service.MatchService;
import com.firstfuel.fafi.service.dto.FranchiseDTO;
import com.firstfuel.fafi.service.dto.FranchiseStandingsDTO;
import com.firstfuel.fafi.service.dto.MatchDTO;

/**
 * <p>
 * </p>
 *
 * @author Narendra Kumar
 * @version 1.0
 * @since 27-Dec-2017
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsResource {
    private static final Logger LOGGER = LoggerFactory.getLogger( TieMatchResource.class );

    @Autowired
    private FranchiseService franchiseService;

    @Autowired
    private MatchService matchService;

    /**
     * GET  /tie-matches : get all the tieMatches.
     *
     * @param seasonId the id of the season
     * @return the ResponseEntity with status 200 (OK) and the list of Franchise standings in body
     */
    @GetMapping("/seasons/{seasonId}/franchise-standings")
    @Timed
    public ResponseEntity<List<FranchiseStandingsDTO>> getFranchiseStandings( @PathVariable Long seasonId ) {
        LOGGER.debug( "REST request to get Franchise standings for seasonId : {}", seasonId );

        final List<FranchiseDTO> franchiseDTOList = franchiseService.getAllFranchiseBySeason( seasonId );
        LOGGER.debug( "franchiseDTOList : {}", franchiseDTOList );

        final Map<Long, List<MatchDTO>> franchiseIdToMatches = matchService.getAllMatchesByFranchises( franchiseDTOList );

        final Map<Long, Double> franchiseIdToPoints = franchiseIdToMatches.entrySet()
            .stream()
            .collect( Collectors.toMap( Map.Entry::getKey, o -> calculatePoints( o.getKey(), o.getValue() ) ) );

        final Map<Long, List<Boolean>> franchiseIdToForm = franchiseIdToMatches.entrySet()
            .stream()
            .collect( Collectors.toMap( Map.Entry::getKey, o -> evaluateCurrentForm( o.getKey(), o.getValue() ) ) );

        AtomicInteger rank = new AtomicInteger( 0 );
        List<FranchiseStandingsDTO> franchiseStandingsDTOList = franchiseDTOList.stream()
            .sorted( Comparator.comparing( FranchiseDTO::getPoints, Comparator.reverseOrder() ) )
            .map( franchiseDTO -> {
                FranchiseStandingsDTO franchiseStandingsDTO = new FranchiseStandingsDTO();
                franchiseStandingsDTO.setRank( rank.incrementAndGet() );
                franchiseStandingsDTO.setFranchise( franchiseDTO );
                franchiseStandingsDTO.setMatchesPlayed(
                    Objects.nonNull( franchiseIdToMatches.get( franchiseDTO.getId() ) ) ? franchiseIdToMatches.get( franchiseDTO.getId() ).size() : 0 );
                franchiseStandingsDTO.setPoints( franchiseIdToPoints.get( franchiseDTO.getId() ) );
                franchiseStandingsDTO.setCurrentForm( franchiseIdToForm.get( franchiseDTO.getId() ) );
                return franchiseStandingsDTO;
            } )
            .collect( Collectors.toList() );
        return new ResponseEntity<>( franchiseStandingsDTOList, HttpStatus.OK );
    }

    private Double calculatePoints( Long franchiseId, List<MatchDTO> matches ) {
        return matches.stream().mapToDouble( match -> {
            if ( Objects.equals( franchiseId, match.getFranchise1Id() ) ) {
                return match.getPointsForFranchise1();
            } else {
                return match.getPointsForFranchise2();
            }
        } ).sum();
    }

    private List<Boolean> evaluateCurrentForm( Long franchiseId, List<MatchDTO> matches ) {
        return matches.stream().filter( matchDTO -> Objects.nonNull( matchDTO.getWinnerId() ) ).map( match -> {
            if ( Objects.equals( franchiseId, match.getWinnerId() ) ) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } ).collect( Collectors.toList() );
    }
}
