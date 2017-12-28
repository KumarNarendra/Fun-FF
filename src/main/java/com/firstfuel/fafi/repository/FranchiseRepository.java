package com.firstfuel.fafi.repository;

import java.util.List;

import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.domain.Season;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Franchise entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FranchiseRepository
    extends JpaRepository<Franchise, Long>, JpaSpecificationExecutor<Franchise> {
    List<Franchise> getAllBySeason( Season season );

}
