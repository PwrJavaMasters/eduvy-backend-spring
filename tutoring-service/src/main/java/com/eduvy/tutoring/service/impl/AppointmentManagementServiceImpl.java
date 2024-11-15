package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.dto.appointment.*;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.model.TutorAvailability;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.model.utils.HoursBlock;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.repository.TutorAvailabilityRepository;
import com.eduvy.tutoring.service.AppointmentManagementService;
import com.eduvy.tutoring.service.PaymentService;
import com.eduvy.tutoring.service.TutorProfileService;
import com.eduvy.tutoring.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.eduvy.tutoring.utils.SecurityContextHolderUtils.getCurrentUserMailFromContext;

@Service
@AllArgsConstructor
public class AppointmentManagementServiceImpl implements AppointmentManagementService {

    TutorProfileService tutorProfileService;
    PaymentService paymentService;
    UserService userService;

    TutorAvailabilityRepository tutorAvailabilityRepository;
    AppointmentRepository appointmentRepository;


    @Override
    public ResponseEntity<BookAppointmentResponse> bookAppointment(BookAppointmentRequest bookAppointmentRequest, String tutorId) {
        String studentMail = getCurrentUserMailFromContext();
        if (studentMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if(bookAppointmentRequest == null)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorId(tutorId);
        if (tutorProfile == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        boolean isAppointmentValid = validateAppointment(bookAppointmentRequest, tutorProfile);
        if (!isAppointmentValid)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        Double price = tutorProfileService.getSubjectPrice(tutorProfile, bookAppointmentRequest.getSubject());
        if (price == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        Appointment  appointment = new Appointment(
                bookAppointmentRequest.getDay(),
                bookAppointmentRequest.getStartDate(),
                bookAppointmentRequest.getEndDate(),
                bookAppointmentRequest.getSubject(),
                price,
                bookAppointmentRequest.getDescription(),
                tutorProfile,
                studentMail
        );

        String meetingUrl = "todo";
//        if (meetingUrl == null) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
//        }

        appointment.setMeetingUrl(meetingUrl);

        appointmentRepository.saveAndFlush(appointment);
        String paymentUrl = generateOneTimePaymentLink(studentMail, appointment);

        return ResponseEntity.ok(new BookAppointmentResponse(paymentUrl));
    }

    private boolean validateAppointment(BookAppointmentRequest request, TutorProfile tutorProfile) {
        LocalDate startDateDay = request.getStartDate().toLocalDateTime().toLocalDate();
        LocalDate endDateDay = request.getEndDate().toLocalDateTime().toLocalDate();
        if (!request.getDay().equals(startDateDay) || !request.getDay().equals(endDateDay)) {
            return false;
        }

        TutorAvailability tutorAvailability = tutorAvailabilityRepository
                .getTutorAvailabilityByTutorAndDay(tutorProfile.getTutorMail(), request.getDay());

        if (tutorAvailability == null) return false;

        List<HoursBlock> hoursBlockList = tutorAvailability.getHoursBlockList();
        if (hoursBlockList == null || hoursBlockList.isEmpty()) return false;

        Timestamp requestStart = request.getStartDate();
        Timestamp requestEnd = request.getEndDate();

        for (HoursBlock block : hoursBlockList) {
            Timestamp blockStart = block.getStartTime();
            Timestamp blockEnd = block.getEndTime();

            if (blockStart == null && blockEnd == null) continue;

            if (!requestStart.before(blockStart) && !requestEnd.after(blockEnd)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ResponseEntity<ConfirmAppointmentResponse> confirmAppointment(ConfirmAppointmentRequest confirmAppointmentRequest) {
        return null; //todo
    }

    @Override
    public ResponseEntity<List<UserAppointmentResponse>> getUserAppointmentsByDay(GetAvailabilityRequest getAvailabilityRequest) {
        String student = getCurrentUserMailFromContext();
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
