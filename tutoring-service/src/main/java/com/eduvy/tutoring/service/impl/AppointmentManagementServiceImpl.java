package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.dto.appointment.*;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.service.AppointmentManagementService;
import com.eduvy.tutoring.service.PaymentService;
import com.eduvy.tutoring.service.TutorProfileService;
import com.eduvy.tutoring.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AppointmentManagementServiceImpl implements AppointmentManagementService {

    TutorProfileService tutorProfileService;

    AppointmentRepository appointmentRepository;
    PaymentService paymentService;
    UserService userService;


    @Override
    public ResponseEntity<BookAppointmentResponse> bookAppointment(BookAppointmentRequest bookAppointmentRequest, String tutorId) {
        String studentMail = userService.getUserMail();
        if (studentMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if(bookAppointmentRequest == null)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorId(tutorId);
        if (tutorProfile == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        //todo do validation if teacher is available and check date

        Double price = tutorProfileService.getSubjectPrice(tutorProfile, bookAppointmentRequest.getSubject());
        if (price == null)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        Appointment appointment = new Appointment(
                bookAppointmentRequest.getDay(),
                bookAppointmentRequest.getStartDate(),
                bookAppointmentRequest.getEndDate(),
                bookAppointmentRequest.getSubject(),
                price,
                bookAppointmentRequest.getDescription(),
                tutorProfile,
                studentMail
        );

        appointmentRepository.saveAndFlush(appointment);
        String paymentUrl = generateOneTimePaymentLink(studentMail, appointment);

        return ResponseEntity.ok(new BookAppointmentResponse(paymentUrl));
    }

    @Override
    public ResponseEntity<ConfirmAppointmentResponse> confirmAppointment(ConfirmAppointmentRequest confirmAppointmentRequest) {
        return null; //todo
    }

    @Override
    public ResponseEntity<List<UserAppointmentResponse>> getUserAppointmentsByDay(GetAvailabilityRequest getAvailabilityRequest) {
        String student = userService.getUserMail();
        if (student == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Appointment> appointments = appointmentRepository.findAppointmentsByStudentAndDay(student, getAvailabilityRequest.getDay());
        if (appointments.isEmpty())
            return ResponseEntity.ok(new ArrayList<>());

        List<UserAppointmentResponse> userAppointmentResponses = appointments.stream()
                .map(this::mapAppointmentToUserAppointmentResponse)
                .toList();

        return ResponseEntity.ok(userAppointmentResponses);
    }

    private UserAppointmentResponse mapAppointmentToUserAppointmentResponse(Appointment appointment) {
        return new UserAppointmentResponse(
                appointment.getDay(),
                appointment.getStartDate(),
                appointment.getEndDate(),
                appointment.getSubject(),
                appointment.getPrice(),
                appointment.getIsConfirmed(),
                "https://meet.google.com/",
                appointment.getDescription(),
                tutorProfileService.getTutorFullName(appointment.getTutorProfile()),
                appointment.getIsPaid(),
                appointment.getIsPaid() ? null : generateOneTimePaymentLink(appointment.getStudent(), appointment)
        );
    }

    public String generateOneTimePaymentLink(String userMail, Appointment appointment) {
        return paymentService.getPaymentUrl("todo"); //todo implement
    }
}