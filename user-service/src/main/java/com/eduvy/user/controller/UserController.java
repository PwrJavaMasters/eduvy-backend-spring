package com.eduvy.user.controller;

import com.eduvy.user.dto.user.details.EditUserDetailsRequest;
import com.eduvy.user.dto.user.details.FillUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsCheckResponse;
import com.eduvy.user.dto.user.details.UserDetailsResponse;
import com.eduvy.user.service.ProfilePictureService;
import com.eduvy.user.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ProfilePictureService profilePictureService;

    public UserController(UserService userService, ProfilePictureService profilePictureService) {
        this.userService = userService;
        this.profilePictureService = profilePictureService;
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

    @PostMapping("/edit-user-details")
    public ResponseEntity<Void> editUserDetails(@RequestBody EditUserDetailsRequest editUserDetailsRequest) {
        return userService.editUserDetails(editUserDetailsRequest);
    }

    @PostMapping(value = "/upload-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        return profilePictureService.uploadProfilePicture(file);
    }

    @GetMapping("/profile-picture")
    public ResponseEntity<byte[]> editUserDetails() {
        return profilePictureService.getProfilePicture();
    }
}
