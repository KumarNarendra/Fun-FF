package com.firstfuel.fafi.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Service;

import com.firstfuel.fafi.domain.Authority;
import com.firstfuel.fafi.domain.User;
import com.firstfuel.fafi.repository.AuthorityRepository;
import com.firstfuel.fafi.repository.UserRepository;
import com.firstfuel.fafi.security.AuthoritiesConstants;

@Service
public class SocialService {

    private final Logger log = LoggerFactory.getLogger( SocialService.class );

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;


    public void deleteUserSocialConnection( String login ) {
        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository( login );
        connectionRepository.findAllConnections().keySet().forEach( providerId -> {
            connectionRepository.removeConnections( providerId );
            log.debug( "Delete user social connection providerId: {}", providerId );
        } );
    }

    public void createSocialUser( Connection<?> connection, String langKey ) {
        if ( connection == null ) {
            log.error( "Cannot create social user because connection is null" );
            throw new IllegalArgumentException( "Connection cannot be null" );
        }
        UserProfile userProfile = connection.fetchUserProfile();
        String providerId = connection.getKey().getProviderId();
        String imageUrl = connection.getImageUrl();
        User user = createUserIfNotExist( userProfile, langKey, providerId, imageUrl );
        createSocialConnection( user.getLogin(), connection );
        mailService.sendSocialRegistrationValidationEmail( user, providerId );
    }

    private User createUserIfNotExist( UserProfile userProfile, String langKey, String providerId, String imageUrl ) {
        String email = userProfile.getEmail();
        String userName = userProfile.getUsername();
        if ( !StringUtils.isBlank( userName ) ) {
            userName = userName.toLowerCase( Locale.ENGLISH );
        }
        if ( StringUtils.isBlank( email ) && StringUtils.isBlank( userName ) ) {
            log.error( "Cannot create social user because email and login are null" );
            throw new IllegalArgumentException( "Email and login cannot be null" );
        }
        if ( StringUtils.isBlank( email ) && userRepository.findOneByLogin( userName ).isPresent() ) {
            log.error( "Cannot create social user because email is null and login already exist, login -> {}", userName );
            throw new IllegalArgumentException( "Email cannot be null with an existing login" );
        }
        if ( !StringUtils.isBlank( email ) ) {
            Optional<User> user = userRepository.findOneByEmailIgnoreCase( email );
            if ( user.isPresent() ) {
                log.info( "User already exist associate the connection to this account" );
                return user.get();
            }
        }

        String login = getLoginDependingOnProviderId( userProfile, providerId );
        String encryptedPassword = passwordEncoder.encode( RandomStringUtils.random( 10 ) );
        Set<Authority> authorities = new HashSet<>( 1 );
        authorities.add( authorityRepository.findOne( AuthoritiesConstants.USER ) );

        User newUser = new User();
        newUser.setLogin( login );
        newUser.setPassword( encryptedPassword );
        newUser.setFirstName( userProfile.getFirstName() );
        newUser.setLastName( userProfile.getLastName() );
        newUser.setEmail( email );
        newUser.setActivated( true );
        newUser.setAuthorities( authorities );
        newUser.setLangKey( langKey );
        newUser.setImageUrl( imageUrl );

        return userRepository.save( newUser );
    }

    /**
     * @return login if provider manage a login like Twitter or GitHub otherwise email address.
     * Because provider like Google or Facebook didn't provide login or login like "12099388847393"
     */
    private String getLoginDependingOnProviderId( UserProfile userProfile, String providerId ) {
        if ( "twitter".equals( providerId ) ) {
            return userProfile.getUsername().toLowerCase();
        } else {
            return userProfile.getEmail();
        }
    }

    private void createSocialConnection( String login, Connection<?> connection ) {
        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository( login );
        connectionRepository.addConnection( connection );
    }
}
