package com.firstfuel.fafi.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.firstfuel.fafi.service.dto.FranchiseDTO;
import com.firstfuel.fafi.service.dto.MatchDTO;

/**
 * Service Interface for managing Match.
 */
public interface MatchService {

    /**
     * Save a match.
     *
     * @param matchDTO the entity to save
     * @return the persisted entity
     */
    MatchDTO save( MatchDTO matchDTO );

    /**
     * Get all the matches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MatchDTO> findAll( Pageable pageable );

    /**
     * Get the "id" match.
     *
     * @param id the id of the entity
     * @return the entity
     */
    MatchDTO findOne( Long id );

    /**
     * Delete the "id" match.
     *
     * @param id the id of the entity
     */
    void delete( Long id );

    Map<Long, List<MatchDTO>> getAllMatchesByFranchises( List<FranchiseDTO> franchiseDTOList );
}
