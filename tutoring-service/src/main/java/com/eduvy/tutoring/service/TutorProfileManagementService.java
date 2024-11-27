package com.eduvy.tutoring.service;

import com.eduvy.tutoring.dto.tutor.profile.CreateTutorProfileRequest;
import com.eduvy.tutoring.dto.tutor.profile.TutorProfileManagementResponse;
import org.springframework.http.ResponseEntity;

public interface TutorProfileManagementService {

    ResponseEntity<TutorProfileManagementResponse> createTutorProfile(CreateTutorProfileRequest createTutorProfileRequest);

    ResponseEntity<TutorProfileManagementResponse> getTutorProfile();

    ResponseEntity<TutorProfileManagementResponse> editTutorProfile(CreateTutorProfileRequest createTutorProfileRequest);
}