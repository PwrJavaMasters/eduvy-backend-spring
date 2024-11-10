package com.eduvy.tutoring.controller;


import com.eduvy.tutoring.dto.availibility.AddAvailabilityBlockRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityResponse;
import com.eduvy.tutoring.dto.tutor.profile.CreateTutorProfileRequest;
import com.eduvy.tutoring.dto.tutor.profile.TutorProfileManagementResponse;
import com.eduvy.tutoring.service.TutorAvailabilityService;
import com.eduvy.tutoring.service.TutorProfileManagementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/tutor-profile")
public class TutorProfileController {

    TutorProfileManagementService tutorProfileService;
    TutorAvailabilityService tutorAvailabilityService;

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

    @PostMapping("/calendar/get-availability")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<GetAvailabilityResponse> getDayAvailability(@RequestBody GetAvailabilityRequest getAvailabilityRequest) {
        return tutorAvailabilityService.getDayAvailability(getAvailabilityRequest);
    }
}