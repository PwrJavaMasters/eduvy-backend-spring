package com.eduvy.tutoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TutoringServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+01:00"));
        SpringApplication.run(TutoringServiceApplication.class, args);
    }

}
