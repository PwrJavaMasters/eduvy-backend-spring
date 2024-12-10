package com.eduvy.user.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServicesURL {

    @Value("${chat-service.url}")
    private String chatServiceUrl;
}
