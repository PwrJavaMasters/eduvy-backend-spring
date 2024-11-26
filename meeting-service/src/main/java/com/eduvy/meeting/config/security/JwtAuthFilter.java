package com.eduvy.meeting.config.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Getter
@Setter
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);

        if (token != null && validateToken(token)) {
            DecodedJWT decodedJWT = decodeToken(token);

            String auth0UserId = decodedJWT.getSubject();
            List<String> roles = decodedJWT.getClaim("user_roles").asList(String.class);
            String nickname = decodedJWT.getClaim("nickname").asString();
            String email = decodedJWT.getClaim("email").asString();

            if (roles == null) {
                roles = Collections.singletonList("ROLE_USER");
            }



            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDetails userDetails = new UserInfoDetails(auth0UserId, email, nickname, "", authorities);


            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(request, response);
    }

    private JwkProvider jwkProvider;

    private final Auth0Properties auth0Properties;

    public JwtAuthFilter(Auth0Properties auth0Properties) {
        this.auth0Properties = auth0Properties;
    }

    @PostConstruct
    public void init() {
        this.jwkProvider = new JwkProviderBuilder(auth0Properties.getIssuer())
                .cached(10, 24, TimeUnit.HOURS) // Cache up to 10 public keys for 24 hours
                .build();
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = jwkProvider.get(jwt.getKeyId());
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(auth0Properties.getIssuer())
                    .withAudience(auth0Properties.getAudience())
                    .build();

            verifier.verify(token);
            return true;
        } catch (Exception e) {
            // Log the exception
            logger.error("JWT validation failed", e);
            return false;
        }
    }

    private DecodedJWT decodeToken(String token) {
        return JWT.decode(token);
    }
}
