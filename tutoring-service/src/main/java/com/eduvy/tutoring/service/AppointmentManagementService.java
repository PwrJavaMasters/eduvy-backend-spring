package com.eduvy.tutoring.service;


import com.eduvy.tutoring.dto.appointment.*;
import com.eduvy.tutoring.dto.availibility.DayRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentManagementService {

    ResponseEntity<BookAppointmentResponse> bookAppointment(BookAppointmentRequest bookAppointmentRequest, String tutorId);

    ResponseEntity<ConfirmAppointmentResponse> confirmAppointment(ConfirmAppointmentRequest confirmAppointmentRequest);

    ResponseEntity<List<UserAppointmentResponse>> getUserAppointmentsByDay(GetAvailabilityRequest getAvailabilityRequest);

    ResponseEntity<List<UserAppointmentResponse>> getUserAppointmentsByMonth(GetAvailabilityRequest getAvailabilityRequest);

    ResponseEntity<List<TutorAppointmentResponse>> getTutorAppointmentsByDay(DayRequest dayRequest);
}