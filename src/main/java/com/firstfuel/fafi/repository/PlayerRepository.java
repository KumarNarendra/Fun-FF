package com.firstfuel.fafi.repository;

import java.util.List;

import com.firstfuel.fafi.domain.Player;
import com.firstfuel.fafi.domain.Season;
import com.firstfuel.fafi.service.dto.SeasonDTO;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Player entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {

    List<Player> getAllByFranchise_Season( Season season );
}
