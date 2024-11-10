package com.eduvy.user.controller;

import com.eduvy.user.dto.user.details.FillUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsResponse;
import com.eduvy.user.service.UserService;
import com.eduvy.user.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private final UserService userService;

    public InternalController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-details/{email}")
    public ResponseEntity<UserDetailsResponse> fillUserDetails(@PathVariable("email") String email) {
        return userService.getUserDetailsByMail(email);
    }
}
