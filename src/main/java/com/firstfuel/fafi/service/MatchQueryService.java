package com.firstfuel.fafi.service;


import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.firstfuel.fafi.domain.Match;
import com.firstfuel.fafi.domain.*; // for static metamodels
import com.firstfuel.fafi.repository.MatchRepository;
import com.firstfuel.fafi.service.dto.MatchCriteria;

import com.firstfuel.fafi.service.dto.MatchDTO;
import com.firstfuel.fafi.service.mapper.MatchMapper;

/**
 * Service for executing complex queries for Match entities in the database.
 * The main input is a {@link MatchCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MatchDTO} or a {@link Page} of {@link MatchDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MatchQueryService extends QueryService<Match> {

    private final Logger log = LoggerFactory.getLogger(MatchQueryService.class);


    private final MatchRepository matchRepository;

    private final MatchMapper matchMapper;

    public MatchQueryService(MatchRepository matchRepository, MatchMapper matchMapper) {
        this.matchRepository = matchRepository;
        this.matchMapper = matchMapper;
    }

    /**
     * Return a {@link List} of {@link MatchDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MatchDTO> findByCriteria(MatchCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Match> specification = createSpecification(criteria);
        return matchMapper.toDto(matchRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MatchDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MatchDTO> findByCriteria(MatchCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Match> specification = createSpecification(criteria);
        final Page<Match> result = matchRepository.findAll(specification, page);
        return result.map(matchMapper::toDto);
    }

    /**
     * Function to convert MatchCriteria to a {@link Specifications}
     */
    private Specifications<Match> createSpecification(MatchCriteria criteria) {
        Specifications<Match> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Match_.id));
            }
            if (criteria.getStartDateTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDateTime(), Match_.startDateTime));
            }
            if (criteria.getEndDateTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDateTime(), Match_.endDateTime));
            }
            if (criteria.getPointsForFranchise1() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPointsForFranchise1(), Match_.pointsForFranchise1));
            }
            if (criteria.getPointsForFranchise2() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPointsForFranchise2(), Match_.pointsForFranchise2));
            }
            if (criteria.getTournamentId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getTournamentId(), Match_.tournament, Tournament_.id));
            }
            if (criteria.getFranchise1Id() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getFranchise1Id(), Match_.franchise1, Franchise_.id));
            }
            if (criteria.getFranchise2Id() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getFranchise2Id(), Match_.franchise2, Franchise_.id));
            }
            if (criteria.getWinnerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getWinnerId(), Match_.winner, Franchise_.id));
            }
        }
        return specification;
    }

}
