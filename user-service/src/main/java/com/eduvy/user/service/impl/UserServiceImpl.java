package com.eduvy.user.service.impl;


import com.eduvy.user.dto.user.details.FillUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsCheckResponse;
import com.eduvy.user.dto.user.details.UserDetailsResponse;
import com.eduvy.user.model.UserDetails;
import com.eduvy.user.repository.UserDetailsRepository;
import com.eduvy.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Override
    public UserDetails getUserFromContext() {
        return null; //TODO FILIPEK <3
    }

    public ResponseEntity<UserDetailsCheckResponse> userDetailsFilled(String email) {
        if (email == null) {
            return ResponseEntity.status(422).build();
        }

        UserDetails userDetails = userDetailsRepository.findByEmail(email);
        if (userDetails == null) {
            return ResponseEntity.ok(new UserDetailsCheckResponse(false));
        }

        boolean userDetailsFilled = userDetails.getEmail() != null &&
                userDetails.getFirstName() != null &&
                userDetails.getLastName() != null &&
                userDetails.getDateOfBirth() != null &&
                userDetails.getIsTeacher() != null &&
                userDetails.getIsStudent() != null &&
                userDetails.getIsNewsletter() != null;

        return ResponseEntity.ok(new UserDetailsCheckResponse(userDetailsFilled));
    }

    public ResponseEntity<UserDetailsResponse> getUserDetails(String email) {
        if (email == null) {
            return ResponseEntity.status(422).build();
        }

        System.out.println("Looking for user details for " + email);
        UserDetails userDetails = userDetailsRepository.findByEmail(email);
        if (userDetails == null) return ResponseEntity.status(404).build();

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse(
                userDetails.getEmail(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getDateOfBirth(),
                userDetails.getIsAdmin(),
                userDetails.getIsTeacher(),
                userDetails.getIsStudent(),
                userDetails.getIsNewsletter()
        );

        return ResponseEntity.ok().body(userDetailsResponse);
    }

    @Transactional
    public ResponseEntity<Void> fillUserDetails(FillUserDetailsRequest fillUserDetailsRequest) {
        if (fillUserDetailsRequest == null || fillUserDetailsRequest.email == null) {
            return ResponseEntity.status(422).build();
        }

        UserDetails userDetails = userDetailsRepository.findByEmail(fillUserDetailsRequest.email);
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setEmail(fillUserDetailsRequest.email);
        }

        if (fillUserDetailsRequest.firstName == null ||
                fillUserDetailsRequest.lastName == null ||
                fillUserDetailsRequest.dateOfBirth == null ||
                fillUserDetailsRequest.isAdmin == null ||
                fillUserDetailsRequest.isTeacher == null ||
                fillUserDetailsRequest.isStudent == null ||
                fillUserDetailsRequest.isNewsletter == null ||
                fillUserDetailsRequest.isDeleted == null) {
            return ResponseEntity.status(422).build();
        }

        userDetails.setUserDetails(fillUserDetailsRequest);
        userDetailsRepository.save(userDetails);

        return ResponseEntity.ok().build();
    }
}
