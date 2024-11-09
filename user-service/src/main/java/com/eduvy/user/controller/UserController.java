package com.eduvy.user.controller;

import com.eduvy.user.controller.dto.FillUserDetailsRequest;
import com.eduvy.user.controller.dto.UserDetailsCheckRequest;
import com.eduvy.user.controller.dto.UserDetailsCheckResponse;
import com.eduvy.user.controller.dto.UserDetailsResponse;
import com.eduvy.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return "User Service is up";
    }

    @GetMapping("/is-filled-details/{email}")
    public ResponseEntity<UserDetailsCheckResponse> filledDetails(@PathVariable("email") String email) {
        return userService.userDetailsFilled(email);
    }

    @GetMapping("/user-details/{email}")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable("email") String email) {
        return userService.getUserDetails(email);
    }

    @PostMapping("/user-details")
    public ResponseEntity<Void> fillUserDetails(@RequestBody FillUserDetailsRequest fillUserDetailsRequest) {
        return userService.fillUserDetails(fillUserDetailsRequest);
    }
}
