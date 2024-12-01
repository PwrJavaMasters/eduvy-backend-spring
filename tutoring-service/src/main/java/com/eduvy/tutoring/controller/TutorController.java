package com.eduvy.tutoring.controller;


import com.eduvy.tutoring.dto.appointment.BookAppointmentRequest;
import com.eduvy.tutoring.dto.appointment.BookAppointmentResponse;
import com.eduvy.tutoring.dto.availibility.DayRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityResponse;
import com.eduvy.tutoring.dto.tutor.get.*;
import com.eduvy.tutoring.service.AppointmentManagementService;
import com.eduvy.tutoring.service.TutorAvailabilityService;
import com.eduvy.tutoring.service.TutorsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("tutoring/tutor")
public class TutorController {

    TutorsService tutorsService;
    AppointmentManagementService appointmentService;
    TutorAvailabilityService tutorAvailabilityService;

    @GetMapping("/all")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<List<AllTutorResponse>> getAllTutorProfiles() {
        return tutorsService.getAllTutors();
    }

    @PostMapping("/all-filtered")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<List<AllTutorResponse>> getAllTutorProfilesFiltered(@RequestBody GetTutorsFilteredRequest getTutorsFilteredRequest) {
        return tutorsService.getAllTutorsFiltered(getTutorsFilteredRequest);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AllTutorResponse>> searchTutorProfilesFiltered(@RequestParam("phrase") String phrase) {
        return tutorsService.searchTutors(phrase);
    }

    @GetMapping("/subjects")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<List<AllSubjectsResponse>> getAllSubjects() {
        return tutorsService.getAllSubjects();
    }

    @GetMapping("/subject/{subject}")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<List<TutorBySubjectResponse>> getTutorsBySubject(@PathVariable("subject") String subject) {
        return tutorsService.getTutorsBySubjects(subject);
    }

    @GetMapping("/profile/{tutorId}")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<GetTutorProfileResponse> getTutorProfile(@PathVariable("tutorId") String tutorId) {
        return tutorsService.getTutorById(tutorId);
    }

    @PostMapping("/profile/{tutorId}/book")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')") // todo decide on auth
    public ResponseEntity<BookAppointmentResponse> bookAppointment(@PathVariable("tutorId") String tutorId,
                                                                   @RequestBody BookAppointmentRequest bookAppointmentRequest) {
        return appointmentService.bookAppointment(bookAppointmentRequest, tutorId);
    }

    @PostMapping("/profile/{tutorId}/get-availability")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<GetAvailabilityResponse> getTutorAvailability(@PathVariable("tutorId") String tutorId,
                                                                        @RequestBody DayRequest getAvailabilityRequest) {
        return tutorAvailabilityService.getDayAvailabilityByTutorId(tutorId, getAvailabilityRequest);
    }
}