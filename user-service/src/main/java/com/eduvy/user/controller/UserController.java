package com.eduvy.user.controller;

import com.eduvy.user.controller.dto.FillUserDetailsRequest;
import com.eduvy.user.controller.dto.UserDetailsCheckRequest;
import com.eduvy.user.controller.dto.UserDetailsCheckResponse;
import com.eduvy.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/test")
    public String test() {
        return "User Service is up";
    }

    @GetMapping("/users/is-filled-details")
    public ResponseEntity<UserDetailsCheckResponse> filledDetails(UserDetailsCheckRequest userDetailsCheckRequest) {
        return userService.userDetailsFilled(userDetailsCheckRequest);
    }

    @GetMapping("/users/user-details")
    public ResponseEntity<Void> getUserDetails() {
        return userService.getUserDetails(); //todo implement (where to take email from)
    }

    @PostMapping("/users/user-details")
    public ResponseEntity<Void> fillUserDetails(FillUserDetailsRequest fillUserDetailsRequest) {
        return userService.fillUserDetails(fillUserDetailsRequest);
    }
}
