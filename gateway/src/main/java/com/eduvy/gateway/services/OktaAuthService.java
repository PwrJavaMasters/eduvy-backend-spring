package com.eduvy.gateway.services;

import com.eduvy.gateway.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OktaAuthService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;

    public OktaAuthService(
            @Value("${okta.oauth2.issuer}") String issuer,
            @Value("${okta.oauth2.client-id}") String clientId,
            @Value("${okta.oauth2.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webClient = WebClient.builder()
                .baseUrl(issuer)
                .defaultHeaders(headers -> headers.setBasicAuth(clientId, clientSecret))
                .build();
    }

    public Mono<String> authenticateUser(String username, String password) {
        return webClient.post()
                .uri("/v1/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("username", username)
                        .with("password", password)
                        .with("scope", "openid profile email"))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    if (response.containsKey("access_token")) {
                        return Mono.just((String) response.get("access_token"));
                    } else {
                        return Mono.error(new RuntimeException("Authentication failed"));
                    }
                });
    }

    public Mono<UserDetails> validateAccessToken(String accessToken) {
        return getUserInfo(accessToken)
                .map(userInfo -> {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    // Extract roles from userInfo if available
                    return new org.springframework.security.core.userdetails.User(
                            userInfo.get("email").toString(),
                            "",
                            authorities
                    );
                });
    }

    public Mono<Map<String, Object>> getUserInfo(String accessToken) {
        return webClient.get()
                .uri("/v1/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(Map.class);
    }

    public Mono<Void> registerUser(RegisterRequest registerRequest) {
        // Use Okta Management API to create user
        WebClient managementClient = WebClient.builder()
                .baseUrl("{yourOktaDomain}")
                .defaultHeader("Authorization", "SSWS {yourApiToken}")
                .build();

        Map<String, Object> userProfile = Map.of(
                "firstName", registerRequest.getFirstName(),
                "lastName", registerRequest.getLastName(),
                "email", registerRequest.getEmail(),
                "login", registerRequest.getEmail()
        );

        Map<String, Object> credentials = Map.of(
                "password", Map.of("value", registerRequest.getPassword())
        );

        Map<String, Object> userRequest = Map.of(
                "profile", userProfile,
                "credentials", credentials
        );

        return managementClient.post()
                .uri("/api/v1/users?activate=true")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
