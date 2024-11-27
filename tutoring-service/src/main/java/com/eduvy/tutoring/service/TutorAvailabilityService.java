package com.eduvy.tutoring.service;


import com.eduvy.tutoring.dto.availibility.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TutorAvailabilityService {

    ResponseEntity<GetAvailabilityResponse> addAvailabilityBlock(AvailabilityBlockRequest availabilityBlockRequest);

    ResponseEntity<Void> addAvailabilityBlockList(List<AvailabilityBlockRequest> availabilityBlockRequestList);

    ResponseEntity<Void> deleteAvailabilityBlockList(List<AvailabilityBlockRequest> deleteAvailabilityBlockRequestList);

    ResponseEntity<GetAvailabilityResponse> getDayAvailability(DayRequest getAvailabilityRequest);

    ResponseEntity<GetMonthAvailabilityResponse> getMonthAvailability(DayRequest getAvailabilityRequest);

    ResponseEntity<GetAvailabilityResponse> getDayAvailabilityByTutorId(String tutorId,DayRequest getAvailabilityRequest);

}