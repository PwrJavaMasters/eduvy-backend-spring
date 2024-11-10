package com.eduvy.user.service;


import com.eduvy.user.dto.user.details.FillUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsCheckResponse;
import com.eduvy.user.dto.user.details.UserDetailsResponse;
import com.eduvy.user.model.UserDetails;
import org.springframework.http.ResponseEntity;


public interface UserService {

    UserDetails getUserFromContext();

    ResponseEntity<UserDetailsCheckResponse> userDetailsFilled();

    ResponseEntity<UserDetailsResponse> getUserDetails();

    ResponseEntity<UserDetailsResponse> getUserDetailsByMail(String mail);

    ResponseEntity<Void> fillUserDetails(FillUserDetailsRequest fillUserDetailsRequest);
}



