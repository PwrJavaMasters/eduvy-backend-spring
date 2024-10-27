package com.eduvy.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.WebFilter;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableWebFluxSecurity
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://88.198.122.46:4200", "http://88.198.122.46:4202"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebFilter corsWebFilter() {
        return new CorsWebFilter(corsConfigurationSource());
    }



    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .csrf().disable()
                .cors(cors -> cors.configurationSource(corsConfigurationSource));
        return http.build();
    }

}

// filtr akceptujący wszystko
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .build();
//    }

// STARY FILTR
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-resources/**", "/register", "/login", "/swagger-ui.html/**")
//                        .permitAll()
//                        .anyExchange().authenticated())
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .build();
//    }