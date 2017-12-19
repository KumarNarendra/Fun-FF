package com.firstfuel.fafi.service;

import com.firstfuel.fafi.service.dto.MatchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    MatchDTO save(MatchDTO matchDTO);

    /**
     * Get all the matches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MatchDTO> findAll(Pageable pageable);

    /**
     * Get the "id" match.
     *
     * @param id the id of the entity
     * @return the entity
     */
    MatchDTO findOne(Long id);

    /**
     * Delete the "id" match.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
