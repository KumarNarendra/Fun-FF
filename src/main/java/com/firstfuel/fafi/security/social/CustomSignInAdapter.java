package com.firstfuel.fafi.security.social;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import com.firstfuel.fafi.security.jwt.TokenProvider;

import io.github.jhipster.config.JHipsterProperties;

public class CustomSignInAdapter
    implements SignInAdapter {

    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger( CustomSignInAdapter.class );

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JHipsterProperties jHipsterProperties;

    @Autowired
    private TokenProvider tokenProvider;


    @Override
    public String signIn( String userId, Connection<?> connection, NativeWebRequest request ) {
        try {
            UserDetails user = userDetailsService.loadUserByUsername( userId );
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken( user, null, user.getAuthorities() );

            SecurityContextHolder.getContext().setAuthentication( authenticationToken );
            String jwt = tokenProvider.createToken( authenticationToken, false );
            ServletWebRequest servletWebRequest = (ServletWebRequest)request;
            servletWebRequest.getResponse().addCookie( getSocialAuthenticationCookie( jwt ) );
        } catch( AuthenticationException ae ) {
            log.error( "Social authentication error" );
            log.trace( "Authentication exception trace: {}", ae );
        }
        return jHipsterProperties.getSocial().getRedirectAfterSignIn();
    }

    private Cookie getSocialAuthenticationCookie( String token ) {
        Cookie socialAuthCookie = new Cookie( "social-authentication", token );
        socialAuthCookie.setSecure( true );
        socialAuthCookie.setPath( "/" );
        socialAuthCookie.setMaxAge( 10 );
        return socialAuthCookie;
    }
}
