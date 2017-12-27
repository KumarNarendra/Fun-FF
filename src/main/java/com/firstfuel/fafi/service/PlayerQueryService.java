package com.firstfuel.fafi.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.Player;
import com.firstfuel.fafi.domain.*; // for static metamodels
import com.firstfuel.fafi.repository.PlayerRepository;
import com.firstfuel.fafi.service.dto.PlayerCriteria;
import com.firstfuel.fafi.service.dto.PlayerDTO;
import com.firstfuel.fafi.service.mapper.PlayerMapper;

import io.github.jhipster.service.QueryService;

/**
 * Service for executing complex queries for Player entities in the database.
 * The main input is a {@link PlayerCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PlayerDTO} or a {@link Page} of {@link PlayerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PlayerQueryService
    extends QueryService<Player> {

    private final Logger log = LoggerFactory.getLogger( PlayerQueryService.class );

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;


    /**
     * Return a {@link List} of {@link PlayerDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findByCriteria( PlayerCriteria criteria ) {
        log.debug( "find by criteria : {}", criteria );
        final Specifications<Player> specification = createSpecification( criteria );
        return playerMapper.toDto( playerRepository.findAll( specification ) );
    }

    /**
     * Return a {@link Page} of {@link PlayerDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PlayerDTO> findByCriteria( PlayerCriteria criteria, Pageable page ) {
        log.debug( "find by criteria : {}, page: {}", criteria, page );
        final Specifications<Player> specification = createSpecification( criteria );
        final Page<Player> result = playerRepository.findAll( specification, page );
        return result.map( playerMapper::toDto );
    }

    /**
     * Function to convert PlayerCriteria to a {@link Specifications}
     */
    private Specifications<Player> createSpecification( PlayerCriteria criteria ) {
        Specifications<Player> specification = Specifications.where( null );
        if ( criteria != null ) {
            if ( criteria.getId() != null ) {
                specification = specification.and( buildSpecification( criteria.getId(), Player_.id ) );
            }
            if ( criteria.getName() != null ) {
                specification = specification.and( buildStringSpecification( criteria.getName(), Player_.name ) );
            }
            if ( criteria.getBasePrice() != null ) {
                specification = specification.and( buildRangeSpecification( criteria.getBasePrice(), Player_.basePrice ) );
            }
            if ( criteria.getBidPrice() != null ) {
                specification = specification.and( buildRangeSpecification( criteria.getBidPrice(), Player_.bidPrice ) );
            }
            /*if (criteria.getOptedGames() != null) {
                specification = specification.and(buildSpecification(criteria.getOptedGames(), Player_.optedGames));
            }*/
            if (criteria.getPoints() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPoints(), Player_.points));
            }
            if ( criteria.getFranchiseId() != null ) {
                specification = specification.and( buildReferringEntitySpecification( criteria.getFranchiseId(), Player_.franchise, Franchise_.id ) );
            }
        }
        return specification;
    }

}
