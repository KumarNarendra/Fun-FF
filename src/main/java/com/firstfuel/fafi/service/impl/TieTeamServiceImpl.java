package com.firstfuel.fafi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.TieTeam;
import com.firstfuel.fafi.repository.TieTeamRepository;
import com.firstfuel.fafi.service.TieTeamService;
import com.firstfuel.fafi.service.dto.TieTeamDTO;
import com.firstfuel.fafi.service.mapper.TieTeamMapper;


/**
 * Service Implementation for managing TieTeam.
 */
@Service
@Transactional
public class TieTeamServiceImpl
    implements TieTeamService {

    private final Logger log = LoggerFactory.getLogger( TieTeamServiceImpl.class );

    @Autowired
    private TieTeamRepository tieTeamRepository;

    @Autowired
    private TieTeamMapper tieTeamMapper;


    /**
     * Save a tieTeam.
     *
     * @param tieTeamDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TieTeamDTO save( TieTeamDTO tieTeamDTO ) {
        log.debug( "Request to save TieTeam : {}", tieTeamDTO );
        TieTeam tieTeam = tieTeamMapper.toEntity( tieTeamDTO );
        tieTeam = tieTeamRepository.save( tieTeam );
        return tieTeamMapper.toDto( tieTeam );
    }

    /**
     * Get all the tieTeams.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TieTeamDTO> findAll( Pageable pageable ) {
        log.debug( "Request to get all TieTeams" );
        return tieTeamRepository.findAll( pageable ).map( tieTeamMapper::toDto );
    }

    /**
     * Get one tieTeam by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public TieTeamDTO findOne( Long id ) {
        log.debug( "Request to get TieTeam : {}", id );
        TieTeam tieTeam = tieTeamRepository.findOneWithEagerRelationships( id );
        return tieTeamMapper.toDto( tieTeam );
    }

    /**
     * Delete the tieTeam by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete( Long id ) {
        log.debug( "Request to delete TieTeam : {}", id );
        tieTeamRepository.delete( id );
    }
}
