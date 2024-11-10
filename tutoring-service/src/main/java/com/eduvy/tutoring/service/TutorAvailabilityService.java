package com.eduvy.tutoring.service;


import com.eduvy.tutoring.dto.availibility.AddAvailabilityBlockRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityResponse;
import org.springframework.http.ResponseEntity;

public interface TutorAvailabilityService {

    ResponseEntity<GetAvailabilityResponse> addAvailabilityBlock(AddAvailabilityBlockRequest addAvailabilityBlockRequest);

    ResponseEntity<GetAvailabilityResponse> getDayAvailability(GetAvailabilityRequest getAvailabilityRequest);

    ResponseEntity<GetAvailabilityResponse> getDayAvailabilityByTutorId(String tutorId,GetAvailabilityRequest getAvailabilityRequest);
}