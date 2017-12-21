package com.firstfuel.fafi.service;

import com.firstfuel.fafi.service.dto.TieMatchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing TieMatch.
 */
public interface TieMatchService {

    /**
     * Save a tieMatch.
     *
     * @param tieMatchDTO the entity to save
     * @return the persisted entity
     */
    TieMatchDTO save(TieMatchDTO tieMatchDTO);

    /**
     * Get all the tieMatches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TieMatchDTO> findAll(Pageable pageable);
    /**
     * Get all the TieMatchDTO where Winner is null.
     *
     * @return the list of entities
     */
    List<TieMatchDTO> findAllWhereWinnerIsNull();

    /**
     * Get the "id" tieMatch.
     *
     * @param id the id of the entity
     * @return the entity
     */
    TieMatchDTO findOne(Long id);

    /**
     * Delete the "id" tieMatch.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
