package com.eduvy.gateway.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private String credentials;

    public AuthenticationToken(String token) {
        super(null);
        this.principal = null;
        this.credentials = token;
        setAuthenticated(false);
    }

    public AuthenticationToken(Object principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
