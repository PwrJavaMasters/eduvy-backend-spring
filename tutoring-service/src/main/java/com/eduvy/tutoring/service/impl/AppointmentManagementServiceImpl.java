package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.dto.appointment.*;
import com.eduvy.tutoring.dto.availibility.DayRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.dto.meeting.MeetingRequest;
import com.eduvy.tutoring.dto.meeting.MeetingResponse;
import com.eduvy.tutoring.dto.user.UserDetails;
import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.model.TutorAvailability;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.model.utils.HoursBlock;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.repository.TutorAvailabilityRepository;
import com.eduvy.tutoring.service.*;
import com.eduvy.tutoring.utils.ServicesURL;
import com.eduvy.tutoring.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    AppointmentService appointmentService;
    private final ServicesURL servicesURL;

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

        String meetingUrl = "not confirmed";
        appointment.setMeetingUrl(meetingUrl);

        appointmentRepository.saveAndFlush(appointment);

        return ResponseEntity.ok().build();
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
    @Transactional
    public ResponseEntity<Void> confirmAppointment(String appointmentId) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorMail(userMail);
        if (tutorProfile == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (appointmentId == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        Appointment appointment = appointmentService.getAppointmentByEncodedId(appointmentId);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MeetingRequest meetingRequest = new MeetingRequest(
                appointment.getId(),
                appointment.getStudent(),
                tutorProfile.getTutorMail()
        );

        String meetingServiceUrl = "http://" + servicesURL.getMeetingServiceUrl() + "/internal/link";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MeetingResponse> response = restTemplate.postForEntity(
                meetingServiceUrl,
                meetingRequest,
                MeetingResponse.class
        );

        String meetingUrl = response.getBody().getLink();
        String paymentUrl = generatePaymentLink(appointment);

        appointment.setMeetingUrl(meetingUrl);
        appointment.setIsConfirmed(true);
        appointment.setPaymentUrl(paymentUrl);
        appointmentRepository.save(appointment);
        System.out.println("Appointment accepted:" + appointment);

        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> cancelAppointment(String appointmentId) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (appointmentId == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        Appointment appointment = appointmentService.getAppointmentByEncodedId(appointmentId);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        appointmentRepository.delete(appointment);
        System.out.println("Appointment canceled:" + appointment);

        return ResponseEntity.ok().build();
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

    @Override
    public ResponseEntity<List<UserAppointmentResponse>> getUserAppointmentsByMonth(GetAvailabilityRequest getAvailabilityRequest) {
        String student = getCurrentUserMailFromContext();
        if (student == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        LocalDate date = getAvailabilityRequest.getDay();
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        List<Appointment> appointments = appointmentRepository.findAppointmentsByStudentAndMonth(
                student,
                startOfMonth,
                endOfMonth
        );

        if (appointments.isEmpty())
            return ResponseEntity.ok(new ArrayList<>());

        List<UserAppointmentResponse> userAppointmentResponses = appointments.stream()
                .map(this::mapAppointmentToUserAppointmentResponse)
                .toList();

        return ResponseEntity.ok(userAppointmentResponses);
    }

    @Override
    public ResponseEntity<List<TutorAppointmentResponse>> getTutorAppointmentsByDay(DayRequest dayRequest) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorMail(userMail);
        if (tutorProfile == null){
            System.err.println("TutorProfile not found, user: " + userMail);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Appointment> appointments = appointmentRepository.findAppointmentsByTutorProfileAndDay(tutorProfile, dayRequest.getDay());
        if (appointments.isEmpty())
            return ResponseEntity.ok(new ArrayList<>());

        List<TutorAppointmentResponse> tutorAppointmentResponses = appointments.stream()
                .map(this::mapAppointmentToTutorAppointmentResponse)
                .toList();

        return ResponseEntity.ok(tutorAppointmentResponses);
    }

    @Override
    public ResponseEntity<List<TutorAppointmentResponse>> getTutorMonthAppointments(DayRequest dayRequest) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorMail(userMail);
        if (tutorProfile == null){
            System.err.println("TutorProfile not found, user: " + userMail);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Appointment> appointments = getTutorAppointmentsByTutorProfileAndMonth(dayRequest.getDay(), tutorProfile);
        if (appointments.isEmpty())
            return ResponseEntity.ok(new ArrayList<>());

        List<TutorAppointmentResponse> tutorAppointmentResponses = appointments.stream()
                .map(this::mapAppointmentToTutorAppointmentResponse)
                .toList();

        return ResponseEntity.ok(tutorAppointmentResponses);
    }

    @Override
    public List<Appointment> getTutorAppointmentsByTutorProfileAndMonth(LocalDate date, TutorProfile tutorProfile) {
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        return appointmentRepository.findAppointmentsByTutorProfileAndMonth(tutorProfile, startOfMonth, endOfMonth);
    }

    private UserAppointmentResponse mapAppointmentToUserAppointmentResponse(Appointment appointment) {
        boolean isFinished = appointment.getEndDate().before(new Timestamp(System.currentTimeMillis()));

        return new UserAppointmentResponse(
                Utils.encodeAppointmentId(appointment),
                appointment.getDay(),
                appointment.getStartDate(),
                appointment.getEndDate(),
                appointment.getSubject(),
                appointment.getPrice(),
                appointment.getIsConfirmed(),
                appointment.getIsConfirmed() ? appointment.getMeetingUrl() : null,
                appointment.getDescription(),
                tutorProfileService.getTutorFullName(appointment.getTutorProfile()),
                appointment.getIsPaid(),
                appointment.getIsPaid() ? null : appointment.getPaymentUrl(),
                appointment.getTutorProfile().getTutorMail(),
                isFinished
        );
    }

    private TutorAppointmentResponse mapAppointmentToTutorAppointmentResponse(Appointment appointment) {
        UserDetails userDetails = userService.getUserDetails(appointment.getStudent());
        String studentName = userDetails != null ? userDetails.getFirstName() + " " + userDetails.getLastName() : null;
        boolean isFinished = appointment.getEndDate().before(new Timestamp(System.currentTimeMillis()));

        return new TutorAppointmentResponse(
                Utils.encodeAppointmentId(appointment),
                appointment.getDay(),
                appointment.getStartDate(),
                appointment.getEndDate(),
                appointment.getSubject(),
                appointment.getPrice(),
                appointment.getIsConfirmed(),
                appointment.getIsConfirmed() ? appointment.getMeetingUrl() : null,
                appointment.getDescription(),
                appointment.getStudent(), //todo think what to return here - was first name + last name
                appointment.getIsPaid(),
                studentName,
                isFinished
        );
    }

    private String generatePaymentLink(Appointment appointment) {
        return paymentService.getPaymentUrl(appointment);
    }
}
