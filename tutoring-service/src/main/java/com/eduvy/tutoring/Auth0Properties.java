package com.eduvy.tutoring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "auth0")
public class Auth0Properties {
    private String audience;
    private String issuer;
    private String jwksUri;
}
