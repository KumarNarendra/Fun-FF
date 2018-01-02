package com.firstfuel.fafi.repository;

import java.util.List;

import com.firstfuel.fafi.domain.Player;
import com.firstfuel.fafi.domain.TieMatch;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the TieMatch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TieMatchRepository extends JpaRepository<TieMatch, Long>, JpaSpecificationExecutor<TieMatch> {

    List<TieMatch> getDistinctByTeam1_TiePlayersInOrTeam2_TiePlayersIn( List<Player> players1, List<Player> players2 );
}
