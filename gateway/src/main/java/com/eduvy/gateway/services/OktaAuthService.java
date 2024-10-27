package com.eduvy.gateway.services;

import com.eduvy.gateway.dto.RegisterRequest;
import com.okta.sdk.client.Client;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OktaAuthService {

    private final Client oktaClient;

    public OktaAuthService(Client oktaClient) {
        this.oktaClient = oktaClient;
    }

    public Mono<String> authenticateUser(String username, String password) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://{yourOktaDomain}")
                .build();

        return webClient.post()
                .uri("/api/v1/authn")
                .bodyValue(Map.of("username", username, "password", password))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    if (response.containsKey("sessionToken")) {
                        return Mono.just((String) response.get("sessionToken"));
                    } else {
                        return Mono.error(new RuntimeException("Authentication failed"));
                    }
                })
                .flatMap(sessionToken -> {
                    return getUserRoles(username)
                            .map(roles -> {
                                // Store roles in session or token
                                // Return session token
                                return sessionToken;
                            });
                });
    }

    public Mono<User> registerUser(RegisterRequest registerRequest) {
        return Mono.fromCallable(() -> {
            User user = UserBuilder.instance()
                    .setEmail(registerRequest.getEmail())
                    .setFirstName(registerRequest.getFirstName())
                    .setLastName(registerRequest.getLastName())
                    .setPassword(registerRequest.getPassword().toCharArray())
                    .buildAndCreate(oktaClient);
            return user;
        });
    }
}
