package com.eduvy.gateway;

import com.eduvy.gateway.security.AuthenticationToken;
import com.eduvy.gateway.services.OktaAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {

    private final OktaAuthService oktaAuthService;

    @Autowired
    public CustomAuthenticationManager(OktaAuthService oktaAuthService) {
        this.oktaAuthService = oktaAuthService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return oktaAuthService.validateAccessToken(token)
                .map(userDetails -> new AuthenticationToken(userDetails, token, userDetails.getAuthorities()));
    }
}
