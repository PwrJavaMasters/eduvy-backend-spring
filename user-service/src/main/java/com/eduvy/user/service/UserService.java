package com.eduvy.user.service;

import com.eduvy.user.dto.user.details.FillUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsCheckResponse;
import com.eduvy.user.dto.user.details.UserDetailsResponse;
import com.eduvy.user.model.UserDetails;
import org.springframework.http.ResponseEntity;

public interface UserService {

    UserDetails getUserFromContext();

    ResponseEntity<UserDetailsCheckResponse> userDetailsFilled(String email);

    ResponseEntity<UserDetailsResponse> getUserDetails(String email);

    ResponseEntity<Void> fillUserDetails(FillUserDetailsRequest fillUserDetailsRequest);
}
