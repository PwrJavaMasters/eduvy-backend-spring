package com.eduvy.tutoring.utils;

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

}
