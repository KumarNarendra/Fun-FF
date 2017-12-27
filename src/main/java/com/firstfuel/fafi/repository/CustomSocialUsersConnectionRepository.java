package com.firstfuel.fafi.repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

import com.firstfuel.fafi.domain.SocialUserConnection;

public class CustomSocialUsersConnectionRepository
    implements UsersConnectionRepository {

    @Autowired
    private SocialUserConnectionRepository socialUserConnectionRepository;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;


    @Override
    public List<String> findUserIdsWithConnection( Connection<?> connection ) {
        ConnectionKey key = connection.getKey();
        List<SocialUserConnection> socialUserConnections = socialUserConnectionRepository.findAllByProviderIdAndProviderUserId( key.getProviderId(), key.getProviderUserId() );
        return socialUserConnections.stream().map( SocialUserConnection::getUserId ).collect( Collectors.toList() );
    }

    @Override
    public Set<String> findUserIdsConnectedTo( String providerId, Set<String> providerUserIds ) {
        List<SocialUserConnection> socialUserConnections = socialUserConnectionRepository.findAllByProviderIdAndProviderUserIdIn( providerId, providerUserIds );
        return socialUserConnections.stream().map( SocialUserConnection::getUserId ).collect( Collectors.toSet() );
    }

    @Override
    public ConnectionRepository createConnectionRepository( String userId ) {
        if ( userId == null ) {
            throw new IllegalArgumentException( "userId cannot be null" );
        }
        return new CustomSocialConnectionRepository( userId );
    }
}
