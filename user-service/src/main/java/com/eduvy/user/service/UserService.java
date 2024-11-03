package com.eduvy.user.service;


import com.eduvy.user.controller.dto.FillUserDetailsRequest;
import com.eduvy.user.controller.dto.UserDetailsCheckResponse;
import com.eduvy.user.controller.dto.UserDetailsCheckRequest;
import com.eduvy.user.model.User;
import com.eduvy.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<UserDetailsCheckResponse> userDetailsFilled(UserDetailsCheckRequest userDetailsCheckRequest) {
        if (userDetailsCheckRequest == null || userDetailsCheckRequest.email == null) {
            return ResponseEntity.status(422).build();
        }

        User user = userRepository.findByEmail(userDetailsCheckRequest.email);
        if (user == null) return ResponseEntity.status(404).build();

        boolean userDetailsFilled = user.getEmail() != null &&
                user.getFirstName() != null &&
                user.getLastName() != null &&
                user.getDateOfBirth() != null &&
                user.getIsTeacher() != null &&
                user.getIsStudent() != null &&
                user.getIsNewsletter() != null;

        return ResponseEntity.ok(new UserDetailsCheckResponse(userDetailsFilled));
    }

    public ResponseEntity<Void> getUserDetails() {
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> fillUserDetails(FillUserDetailsRequest fillUserDetailsRequest) {
        if (fillUserDetailsRequest == null || fillUserDetailsRequest.email == null) {
            return ResponseEntity.status(422).build();
        }

        User user = userRepository.findByEmail(fillUserDetailsRequest.email);
        if (user == null) return ResponseEntity.status(404).build();

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

        user.setUserDetails(fillUserDetailsRequest);

        return ResponseEntity.ok().build();
    }
}
