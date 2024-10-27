package com.eduvy.gateway;

import com.eduvy.gateway.services.OktaAuthService;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SessionAuthenticationConverter implements ServerAuthenticationConverter {

    private final OktaAuthService oktaAuthService;

    public SessionAuthenticationConverter(OktaAuthService oktaAuthService) {
        this.oktaAuthService = oktaAuthService;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        HttpCookie sessionCookie = exchange.getRequest().getCookies().getFirst("SESSION");
        if (sessionCookie != null) {
            String sessionToken = sessionCookie.getValue();
            // Validate session token with Okta or your own session store
            return oktaAuthService.validateSessionToken(sessionToken)
                    .map(userDetails -> new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()));
        }
        return Mono.empty();
    }
}
