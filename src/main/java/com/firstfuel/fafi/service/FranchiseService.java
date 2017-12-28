package com.firstfuel.fafi.service;

import java.util.List;

import com.firstfuel.fafi.service.dto.FranchiseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Franchise.
 */
public interface FranchiseService {

    /**
     * Save a franchise.
     *
     * @param franchiseDTO the entity to save
     * @return the persisted entity
     */
    FranchiseDTO save( FranchiseDTO franchiseDTO );

    /**
     * Get all the franchises.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<FranchiseDTO> findAll( Pageable pageable );

    /**
     * Get the "id" franchise.
     *
     * @param id the id of the entity
     * @return the entity
     */
    FranchiseDTO findOne( Long id );

    /**
     * Delete the "id" franchise.
     *
     * @param id the id of the entity
     */
    void delete( Long id );

    /**
     * Save points for a franchise.
     *
     * @param franchiseId the id of the entity to save
     * @param points      points earned by entity
     * @return the persisted entity
     */
    void savePointsForFranchise( Long franchiseId, Double points );

    /**
     * Get all the franchises without pageable.
     *
     * @return the list of entities
     */
    List<FranchiseDTO> getAllFranchiseBySeason(Long seasonId);
}
