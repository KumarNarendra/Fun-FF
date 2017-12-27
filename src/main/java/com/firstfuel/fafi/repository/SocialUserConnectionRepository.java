package com.firstfuel.fafi.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firstfuel.fafi.domain.SocialUserConnection;

/**
 * Spring Data JPA repository for the Social User Connection entity.
 */
@Repository
public interface SocialUserConnectionRepository
    extends JpaRepository<SocialUserConnection, Long> {

    List<SocialUserConnection> findAllByProviderIdAndProviderUserId( String providerId, String providerUserId );

    List<SocialUserConnection> findAllByProviderIdAndProviderUserIdIn( String providerId, Set<String> providerUserIds );

    List<SocialUserConnection> findAllByUserIdOrderByProviderIdAscRankAsc( String userId );

    List<SocialUserConnection> findAllByUserIdAndProviderIdOrderByRankAsc( String userId, String providerId );

    List<SocialUserConnection> findAllByUserIdAndProviderIdAndProviderUserIdIn( String userId, String providerId, List<String> provideUserId );

    SocialUserConnection findOneByUserIdAndProviderIdAndProviderUserId( String userId, String providerId, String providerUserId );

    void deleteByUserIdAndProviderId( String userId, String providerId );

    void deleteByUserIdAndProviderIdAndProviderUserId( String userId, String providerId, String providerUserId );
}
