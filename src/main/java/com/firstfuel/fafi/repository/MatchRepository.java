package com.firstfuel.fafi.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.domain.Match;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Match entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MatchRepository
    extends JpaRepository<Match, Long>, JpaSpecificationExecutor<Match> {

    List<Match> getMatchesByFranchise1InOrFranchise2In( Collection<Franchise> franchise1, Collection<Franchise> franchise2 );

}
