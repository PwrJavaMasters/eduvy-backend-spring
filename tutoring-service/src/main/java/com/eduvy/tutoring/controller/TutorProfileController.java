package com.eduvy.tutoring.controller;


import com.eduvy.tutoring.dto.appointment.TutorAppointmentResponse;
import com.eduvy.tutoring.dto.availibility.AddAvailabilityBlockRequest;
import com.eduvy.tutoring.dto.availibility.DayRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityResponse;
import com.eduvy.tutoring.dto.tutor.profile.CreateTutorProfileRequest;
import com.eduvy.tutoring.dto.tutor.profile.TutorProfileManagementResponse;
import com.eduvy.tutoring.service.AppointmentManagementService;
import com.eduvy.tutoring.service.TutorAvailabilityService;
import com.eduvy.tutoring.service.TutorProfileManagementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("tutoring/tutor-profile")
public class TutorProfileController {

    TutorProfileManagementService tutorProfileService;
    TutorAvailabilityService tutorAvailabilityService;
    AppointmentManagementService appointmentManagementService;

    @PostMapping("/create")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<TutorProfileManagementResponse> createTutorProfile(@RequestBody CreateTutorProfileRequest createTutorProfileRequest) {
        return tutorProfileService.createTutorProfile(createTutorProfileRequest);
    }

    @GetMapping("/get")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<TutorProfileManagementResponse> getTutorProfile() {
        return tutorProfileService.getTutorProfile();
    }

    @PostMapping("/edit")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<TutorProfileManagementResponse> editTutorProfile(@RequestBody CreateTutorProfileRequest createTutorProfileRequest) {
        return tutorProfileService.editTutorProfile(createTutorProfileRequest);
    }

    @PostMapping("/calendar/add-block")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<GetAvailabilityResponse> addAvailabilityBlock(@RequestBody AddAvailabilityBlockRequest addAvailabilityBlockRequest) {
        return tutorAvailabilityService.addAvailabilityBlock(addAvailabilityBlockRequest);
    }

    @PostMapping("/calendar/add-block-list")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Void> addAvailabilityBlock(@RequestBody List<AddAvailabilityBlockRequest> addAvailabilityBlockRequestList) {
        return tutorAvailabilityService.addAvailabilityBlockList(addAvailabilityBlockRequestList);
    }

    @PostMapping("/calendar/get-day-availability")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<GetAvailabilityResponse> getDayAvailability(@RequestBody DayRequest getAvailabilityRequest) {
        return tutorAvailabilityService.getDayAvailability(getAvailabilityRequest);
    }

    @PostMapping("/calendar/get-availability-month")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<List<GetAvailabilityResponse>> getMonthAvailability(@RequestBody DayRequest getAvailabilityRequest) {
        return tutorAvailabilityService.getMonthAvailability(getAvailabilityRequest);
    }

    @PostMapping("/get-appointments")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<List<TutorAppointmentResponse>> getAppointments(@RequestBody DayRequest getAvailabilityRequest) {
        return appointmentManagementService.getTutorAppointmentsByDay(getAvailabilityRequest);
    }
}