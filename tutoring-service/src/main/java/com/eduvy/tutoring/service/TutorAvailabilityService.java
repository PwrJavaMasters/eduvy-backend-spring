package com.eduvy.tutoring.service;


import com.eduvy.tutoring.dto.availibility.AddAvailabilityBlockRequest;
import com.eduvy.tutoring.dto.availibility.DayRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TutorAvailabilityService {

    ResponseEntity<GetAvailabilityResponse> addAvailabilityBlock(AddAvailabilityBlockRequest addAvailabilityBlockRequest);

    ResponseEntity<Void> addAvailabilityBlockList(List<AddAvailabilityBlockRequest> addAvailabilityBlockRequestList);

    ResponseEntity<GetAvailabilityResponse> getDayAvailability(DayRequest getAvailabilityRequest);

    ResponseEntity<List<GetAvailabilityResponse>> getMonthAvailability(DayRequest getAvailabilityRequest);

    ResponseEntity<GetAvailabilityResponse> getDayAvailabilityByTutorId(String tutorId,DayRequest getAvailabilityRequest);
}