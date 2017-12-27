package com.firstfuel.fafi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.repository.FranchiseRepository;
import com.firstfuel.fafi.service.FranchiseService;
import com.firstfuel.fafi.service.dto.FranchiseDTO;
import com.firstfuel.fafi.service.mapper.FranchiseMapper;


/**
 * Service Implementation for managing Franchise.
 */
@Service
@Transactional
public class FranchiseServiceImpl
    implements FranchiseService {

    private final Logger log = LoggerFactory.getLogger( FranchiseServiceImpl.class );

    @Autowired
    private FranchiseRepository franchiseRepository;

    @Autowired
    private FranchiseMapper franchiseMapper;


    /**
     * Save a franchise.
     *
     * @param franchiseDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public FranchiseDTO save( FranchiseDTO franchiseDTO ) {
        log.debug( "Request to save Franchise : {}", franchiseDTO );
        Franchise franchise = franchiseMapper.toEntity( franchiseDTO );
        franchise = franchiseRepository.save( franchise );
        return franchiseMapper.toDto( franchise );
    }

    /**
     * Get all the franchises.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FranchiseDTO> findAll( Pageable pageable ) {
        log.debug( "Request to get all Franchises" );
        return franchiseRepository.findAll( pageable ).map( franchiseMapper::toDto );
    }

    /**
     * Get one franchise by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public FranchiseDTO findOne( Long id ) {
        log.debug( "Request to get Franchise : {}", id );
        Franchise franchise = franchiseRepository.findOne( id );
        return franchiseMapper.toDto( franchise );
    }

    /**
     * Delete the franchise by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete( Long id ) {
        log.debug( "Request to delete Franchise : {}", id );
        franchiseRepository.delete( id );
    }

    @Override
    public void savePointsForFranchise( Long franchiseId, Double points ) {
        FranchiseDTO franchiseDTO = findOne( franchiseId );
        franchiseDTO.setPoints( franchiseDTO.getPoints() + points );
        save( franchiseDTO );
    }
}
