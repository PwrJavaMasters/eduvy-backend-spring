package com.eduvy.payment.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServicesURL {

    @Value("${user-service.url}")
    private String userServiceUrl;

    @Value("${meeting-service.url}")
    private String meetingServiceUrl;

    @Value("${tutoring-service.url}")
    private String tutoringServiceUrl;

    @Value("${application.url}")
    private String applicationUrl;
}
