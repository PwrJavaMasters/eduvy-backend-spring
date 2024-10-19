package com.eduvy.authorization.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {
    @GetMapping("/auth/test")
    public String test() {
        return "Authorization Service is up";
    }
}
