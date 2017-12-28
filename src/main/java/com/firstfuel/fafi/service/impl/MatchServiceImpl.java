package com.firstfuel.fafi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.domain.Match;
import com.firstfuel.fafi.repository.MatchRepository;
import com.firstfuel.fafi.service.FranchiseService;
import com.firstfuel.fafi.service.MatchService;
import com.firstfuel.fafi.service.dto.FranchiseDTO;
import com.firstfuel.fafi.service.dto.MatchDTO;
import com.firstfuel.fafi.service.dto.TieMatchDTO;
import com.firstfuel.fafi.service.mapper.FranchiseMapper;
import com.firstfuel.fafi.service.mapper.MatchMapper;


/**
 * Service Implementation for managing Match.
 */
@Service
@Transactional
public class MatchServiceImpl
    implements MatchService {

    private final Logger log = LoggerFactory.getLogger( MatchServiceImpl.class );

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private FranchiseService franchiseService;

    @Autowired
    private FranchiseMapper franchiseMapper;


    /**
     * Save a match.
     *
     * @param matchDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public MatchDTO save( MatchDTO matchDTO ) {
        log.debug( "Request to save Match : {}", matchDTO );
        Match match = matchMapper.toEntity( matchDTO );
        match = matchRepository.save( match );
        savePointsForFranchises( matchDTO );
        return matchMapper.toDto( match );
    }

    private void savePointsForFranchises( MatchDTO matchDTO ) {
        if ( Objects.nonNull( matchDTO.getPointsForFranchise1() ) ) {
            franchiseService.savePointsForFranchise( matchDTO.getFranchise1Id(), matchDTO.getPointsForFranchise1() );
        }
        if ( Objects.nonNull( matchDTO.getPointsForFranchise2() ) ) {
            franchiseService.savePointsForFranchise( matchDTO.getFranchise2Id(), matchDTO.getPointsForFranchise2() );
        }

    }

    /**
     * Get all the matches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MatchDTO> findAll( Pageable pageable ) {
        log.debug( "Request to get all Matches" );
        return matchRepository.findAll( pageable ).map( matchMapper::toDto );
    }

    /**
     * Get one match by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public MatchDTO findOne( Long id ) {
        log.debug( "Request to get Match : {}", id );
        Match match = matchRepository.findOne( id );
        return matchMapper.toDto( match );
    }

    /**
     * Delete the match by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete( Long id ) {
        log.debug( "Request to delete Match : {}", id );
        matchRepository.delete( id );
    }

    @Override
    public Map<Long, List<MatchDTO>> getAllMatchesByFranchises( List<FranchiseDTO> franchiseDTOList ) {
        List<Franchise> franchises = franchiseMapper.toEntity( franchiseDTOList );
        List<Match> matches = matchRepository.getMatchesByFranchise1InOrFranchise2In( franchises, franchises );
        Map<Franchise, List<Match>> franchise1Matches = matches.stream().collect( Collectors.groupingBy( Match::getFranchise1 ) );
        Map<Franchise, List<Match>> franchise2Matches = matches.stream().collect( Collectors.groupingBy( Match::getFranchise2 ) );
        Map<Long, List<MatchDTO>> allMatchesByFranchise = Stream.concat( franchise1Matches.entrySet().stream(), franchise2Matches.entrySet().stream() )
            .collect( Collectors.toMap( o -> o.getKey().getId(), o -> matchMapper.toDto( o.getValue() ), this::mergeFunction ) );
        log.debug( "allMatchesByFranchise : {}", allMatchesByFranchise );
        return allMatchesByFranchise;
    }

    private List<MatchDTO> mergeFunction( List<MatchDTO> v1, List<MatchDTO> v2 ) {
        return Stream.concat( v1.stream(), v2.stream() ).distinct().collect( Collectors.toList() );
    }
}
