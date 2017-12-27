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

import com.firstfuel.fafi.domain.TieTeam;
import com.firstfuel.fafi.domain.*; // for static metamodels
import com.firstfuel.fafi.repository.TieTeamRepository;
import com.firstfuel.fafi.service.dto.TieTeamCriteria;
import com.firstfuel.fafi.service.dto.TieTeamDTO;
import com.firstfuel.fafi.service.mapper.TieTeamMapper;

import io.github.jhipster.service.QueryService;

/**
 * Service for executing complex queries for TieTeam entities in the database.
 * The main input is a {@link TieTeamCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TieTeamDTO} or a {@link Page} of {@link TieTeamDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TieTeamQueryService
    extends QueryService<TieTeam> {

    private final Logger log = LoggerFactory.getLogger( TieTeamQueryService.class );

    @Autowired
    private TieTeamRepository tieTeamRepository;

    @Autowired
    private TieTeamMapper tieTeamMapper;


    /**
     * Return a {@link List} of {@link TieTeamDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TieTeamDTO> findByCriteria( TieTeamCriteria criteria ) {
        log.debug( "find by criteria : {}", criteria );
        final Specifications<TieTeam> specification = createSpecification( criteria );
        return tieTeamMapper.toDto( tieTeamRepository.findAll( specification ) );
    }

    /**
     * Return a {@link Page} of {@link TieTeamDTO} which matches the criteria from the database
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TieTeamDTO> findByCriteria( TieTeamCriteria criteria, Pageable page ) {
        log.debug( "find by criteria : {}, page: {}", criteria, page );
        final Specifications<TieTeam> specification = createSpecification( criteria );
        final Page<TieTeam> result = tieTeamRepository.findAll( specification, page );
        return result.map( tieTeamMapper::toDto );
    }

    /**
     * Function to convert TieTeamCriteria to a {@link Specifications}
     */
    private Specifications<TieTeam> createSpecification( TieTeamCriteria criteria ) {
        Specifications<TieTeam> specification = Specifications.where( null );
        if ( criteria != null ) {
            if ( criteria.getId() != null ) {
                specification = specification.and( buildSpecification( criteria.getId(), TieTeam_.id ) );
            }
            if ( criteria.getPoints() != null ) {
                specification = specification.and( buildRangeSpecification( criteria.getPoints(), TieTeam_.points ) );
            }
            if ( criteria.getName() != null ) {
                specification = specification.and( buildStringSpecification( criteria.getName(), TieTeam_.name ) );
            }
            if ( criteria.getTiePlayersId() != null ) {
                specification = specification.and( buildReferringEntitySpecification( criteria.getTiePlayersId(), TieTeam_.tiePlayers, Player_.id ) );
            }
            if ( criteria.getFranchiseId() != null ) {
                specification = specification.and( buildReferringEntitySpecification( criteria.getFranchiseId(), TieTeam_.franchise, Franchise_.id ) );
            }
        }
        return specification;
    }

}
