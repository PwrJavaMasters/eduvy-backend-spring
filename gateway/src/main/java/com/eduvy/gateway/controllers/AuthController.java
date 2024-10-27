package com.eduvy.gateway.controllers;

import com.eduvy.gateway.dto.LoginRequest;
import com.eduvy.gateway.dto.RegisterRequest;
import com.eduvy.gateway.services.OktaAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final OktaAuthService oktaAuthService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(OktaAuthService oktaAuthService, PasswordEncoder passwordEncoder) {
        this.oktaAuthService = oktaAuthService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Void>> login(@RequestBody LoginRequest loginRequest) {
        return oktaAuthService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword())
                .map(sessionToken -> {
                    ResponseCookie cookie = ResponseCookie.from("SESSION", sessionToken)
                            .httpOnly(true)
                            .secure(true) // Set to true in production
                            .path("/")
                            .maxAge(7 * 24 * 60 * 60) // 7 days
                            .build();
                    return ResponseEntity.ok()
                            .header("Set-Cookie", cookie.toString())
                            .build();
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> register(@RequestBody RegisterRequest registerRequest) {
        return oktaAuthService.registerUser(registerRequest)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).build())
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout() {
        ResponseCookie cookie = ResponseCookie.from("SESSION", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return Mono.just(ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .build());
    }
}