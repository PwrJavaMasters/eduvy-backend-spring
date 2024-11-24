package com.eduvy.chat.config.security.websocket;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.eduvy.chat.config.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtAuthFilter jwtAuthFilter;

    public WebSocketAuthInterceptor(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    private final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        logger.info("Attempting WebSocket handshake...");
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String token = jwtAuthFilter.getJwtFromRequest(servletRequest);
            logger.info("Authorization Header: {}", token);
            if (token != null && jwtAuthFilter.validateToken(token)) {
                DecodedJWT decodedJWT = jwtAuthFilter.decodeToken(token);
                attributes.put("auth0UserId", decodedJWT.getSubject());
                attributes.put("userRoles", decodedJWT.getClaim("user_roles").asList(String.class));
                return true;
            }
        }
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
