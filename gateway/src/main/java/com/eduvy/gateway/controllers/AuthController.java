package com.eduvy.gateway.controllers;

import com.eduvy.gateway.dto.LoginRequest;
import com.eduvy.gateway.dto.RegisterRequest;
import com.eduvy.gateway.services.OktaAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final OktaAuthService oktaAuthService;

    public AuthController(OktaAuthService oktaAuthService) {
        this.oktaAuthService = oktaAuthService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody LoginRequest loginRequest) {
        return oktaAuthService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword())
                .map(accessToken -> {
                    ResponseCookie cookie = ResponseCookie.from("SESSION", accessToken)
                            .httpOnly(true)
                            .secure(false) // Set to true in production
                            .path("/")
                            .maxAge(7 * 24 * 60 * 60) // 7 days
                            .build();
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .build();
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Object>> register(@RequestBody RegisterRequest registerRequest) {
        return oktaAuthService.registerUser(registerRequest)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout() {
        ResponseCookie cookie = ResponseCookie.from("SESSION", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build());
    }
}
