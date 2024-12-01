package com.eduvy.user.service.impl;

import com.eduvy.user.dto.tutor.profile.EditUserUpdateRequest;
import com.eduvy.user.dto.user.details.EditUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsCheckResponse;
import com.eduvy.user.model.UserDetails;
import com.eduvy.user.repository.UserDetailsRepository;
import com.eduvy.user.service.UserService;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.eduvy.user.dto.user.details.FillUserDetailsRequest;
import com.eduvy.user.dto.user.details.UserDetailsResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.eduvy.user.utils.SecurityContextHolderUtils.getCurrentUserMailFromContext;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDetailsRepository userDetailsRepository;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final Gson gson = new Gson();

    @Override
    public UserDetails getUserFromContext() {
        return null;
    }

    public ResponseEntity<UserDetailsCheckResponse> userDetailsFilled() {

        String email = getCurrentUserMailFromContext();

        if (email == null) {
            return ResponseEntity.status(422).build();
        }

        UserDetails userData = userDetailsRepository.findByEmail(email);
        if (userData == null) {
            return ResponseEntity.ok(new UserDetailsCheckResponse(false));
        }

        boolean userDetailsFilled = userData.getEmail() != null &&
                userData.getFirstName() != null &&
                userData.getLastName() != null &&
                userData.getDateOfBirth() != null &&
                userData.getIsTeacher() != null &&
                userData.getIsStudent() != null;

        return ResponseEntity.ok(new UserDetailsCheckResponse(userDetailsFilled));
    }

    public ResponseEntity<UserDetailsResponse> getUserDetails() {

        String email = getCurrentUserMailFromContext();

        if (email == null) {
            return ResponseEntity.status(422).build();
        }

        System.out.println("Looking for user details for " + email);
        UserDetails userData = userDetailsRepository.findByEmail(email);
        if (userData == null) return ResponseEntity.status(404).build();

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse(
                userData.getEmail(),
                userData.getFirstName(),
                userData.getLastName(),
                userData.getDateOfBirth(),
                userData.getIsAdmin(),
                userData.getIsTeacher(),
                userData.getIsStudent()
        );

        return ResponseEntity.ok().body(userDetailsResponse);
    }

    @Override
    public ResponseEntity<UserDetailsResponse> getUserDetailsByMail(String email) {
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
                userDetails.getIsStudent()
        );

        return ResponseEntity.ok().body(userDetailsResponse);
    }

    @Transactional
    public ResponseEntity<Void> fillUserDetails(FillUserDetailsRequest fillUserDetailsRequest) {
        if (fillUserDetailsRequest == null || fillUserDetailsRequest.email == null) {
            return ResponseEntity.status(422).build();
        }

        UserDetails userData = userDetailsRepository.findByEmail(fillUserDetailsRequest.email);
        if (userData == null) {
            userData = new UserDetails();
            userData.setEmail(fillUserDetailsRequest.email);
        }

        if (fillUserDetailsRequest.firstName == null ||
                fillUserDetailsRequest.lastName == null ||
                fillUserDetailsRequest.dateOfBirth == null ||
                fillUserDetailsRequest.isTeacher == null ||
                fillUserDetailsRequest.isStudent == null) {
            return ResponseEntity.status(422).build();
        }

        userData.setUserDetails(fillUserDetailsRequest);
        userDetailsRepository.save(userData);

        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> editUserDetails(EditUserDetailsRequest editUserDetailsRequest) {
        UserDetails userDetails = getUserFromContext();
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (editUserDetailsRequest.firstName == null ||
            editUserDetailsRequest.lastName == null) {

            return ResponseEntity.status(422).build();
        }

        boolean updateTutoringService = editUserUpdateInTutoringService(editUserDetailsRequest, userDetails);
        if (!updateTutoringService) {
            return ResponseEntity.status(422).build(); //todo 422 or 500???
        }

        userDetails.setFirstName(editUserDetailsRequest.firstName);
        userDetails.setLastName(editUserDetailsRequest.lastName);
        userDetailsRepository.save(userDetails);

        return ResponseEntity.ok().build();
    }

    private boolean editUserUpdateInTutoringService(EditUserDetailsRequest editUserDetailsRequest, UserDetails userDetails) {
        EditUserUpdateRequest editUserUpdateRequest = new EditUserUpdateRequest(
                userDetails.getEmail(),
                editUserDetailsRequest.firstName,
                editUserDetailsRequest.lastName
        );

        String url = "http://localhost:8085/internal/edit-user-update";

        String jsonPayload = gson.toJson(editUserUpdateRequest);

        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/json");

        request.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.printf("Request url: %s | Status code: %d%n", url, statusCode);

            return statusCode == 200;

        } catch (IOException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            return false;
        }
    }
}
