package com.firstfuel.fafi.service.impl;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.Match;
import com.firstfuel.fafi.repository.MatchRepository;
import com.firstfuel.fafi.service.FranchiseService;
import com.firstfuel.fafi.service.MatchService;
import com.firstfuel.fafi.service.dto.MatchDTO;
import com.firstfuel.fafi.service.dto.TieMatchDTO;
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
}
