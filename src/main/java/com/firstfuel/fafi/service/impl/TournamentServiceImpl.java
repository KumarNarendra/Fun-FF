package com.firstfuel.fafi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.Tournament;
import com.firstfuel.fafi.repository.TournamentRepository;
import com.firstfuel.fafi.service.TournamentService;
import com.firstfuel.fafi.service.dto.TournamentDTO;
import com.firstfuel.fafi.service.mapper.TournamentMapper;


/**
 * Service Implementation for managing Tournament.
 */
@Service
@Transactional
public class TournamentServiceImpl
    implements TournamentService {

    private final Logger log = LoggerFactory.getLogger( TournamentServiceImpl.class );

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentMapper tournamentMapper;


    /**
     * Save a tournament.
     *
     * @param tournamentDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TournamentDTO save( TournamentDTO tournamentDTO ) {
        log.debug( "Request to save Tournament : {}", tournamentDTO );
        Tournament tournament = tournamentMapper.toEntity( tournamentDTO );
        tournament = tournamentRepository.save( tournament );
        return tournamentMapper.toDto( tournament );
    }

    /**
     * Get all the tournaments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TournamentDTO> findAll( Pageable pageable ) {
        log.debug( "Request to get all Tournaments" );
        return tournamentRepository.findAll( pageable ).map( tournamentMapper::toDto );
    }

    /**
     * Get one tournament by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public TournamentDTO findOne( Long id ) {
        log.debug( "Request to get Tournament : {}", id );
        Tournament tournament = tournamentRepository.findOne( id );
        return tournamentMapper.toDto( tournament );
    }

    /**
     * Delete the tournament by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete( Long id ) {
        log.debug( "Request to delete Tournament : {}", id );
        tournamentRepository.delete( id );
    }
}
