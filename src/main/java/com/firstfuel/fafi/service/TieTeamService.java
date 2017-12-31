package com.firstfuel.fafi.service;

import com.firstfuel.fafi.service.dto.TieTeamDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing TieTeam.
 */
public interface TieTeamService {

    /**
     * Save a tieTeam.
     *
     * @param tieTeamDTO the entity to save
     * @return the persisted entity
     */
    TieTeamDTO save( TieTeamDTO tieTeamDTO );

    /**
     * Get all the tieTeams.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TieTeamDTO> findAll( Pageable pageable );

    /**
     * Get the "id" tieTeam.
     *
     * @param id the id of the entity
     * @return the entity
     */
    TieTeamDTO findOne( Long id );

    /**
     * Delete the "id" tieTeam.
     *
     * @param id the id of the entity
     */
    void delete( Long id );

}
