package com.eduvy.tutoring.controller;


import com.eduvy.tutoring.dto.appointment.UserAppointmentResponse;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.service.AppointmentManagementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("tutoring/calendar")
public class StudentAppointmentController {

    AppointmentManagementService appointmentService;

    @PostMapping("/get-appointments")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<List<UserAppointmentResponse>> getStudentAppointmentsByDay(@RequestBody GetAvailabilityRequest getAvailabilityRequest) {
        return appointmentService.getUserAppointmentsByDay(getAvailabilityRequest);
    }

    @PostMapping("/get-appointments-monthly")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<List<UserAppointmentResponse>> getStudentAppointmentsByMonth(@RequestBody GetAvailabilityRequest getAvailabilityRequest) {
        return appointmentService.getUserAppointmentsByMonth(getAvailabilityRequest);
    }
}