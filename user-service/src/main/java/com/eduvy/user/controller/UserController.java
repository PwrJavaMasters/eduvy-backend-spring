package com.eduvy.user.controller;

import com.eduvy.user.dto.user.details.FillUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsCheckResponse;
import com.eduvy.user.dto.user.details.UserDetailsResponse;
import com.eduvy.user.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return "User Service is up";
    }

    @GetMapping("/is-filled-details")
    public ResponseEntity<UserDetailsCheckResponse> filledDetails() {
        return userService.userDetailsFilled();
    }

    @GetMapping("/user-details")
    public ResponseEntity<UserDetailsResponse> getUserDetails() {
        return userService.getUserDetails();
    }

    @PostMapping("/user-details")
    public ResponseEntity<Void> fillUserDetails(@RequestBody FillUserDetailsRequest fillUserDetailsRequest) {
        return userService.fillUserDetails(fillUserDetailsRequest);
    }
}
