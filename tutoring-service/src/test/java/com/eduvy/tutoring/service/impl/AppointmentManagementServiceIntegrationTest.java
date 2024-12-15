package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.config.security.UserInfoDetails;
import com.eduvy.tutoring.dto.appointment.*;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.model.TutorAvailability;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.model.utils.HoursBlock;
import com.eduvy.tutoring.model.utils.Subject;
import com.eduvy.tutoring.model.utils.SubjectData;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.repository.TutorAvailabilityRepository;
import com.eduvy.tutoring.repository.TutorProfileRepository;
import com.eduvy.tutoring.service.AppointmentManagementService;
import com.eduvy.tutoring.service.TutorProfileService;
import com.eduvy.tutoring.utils.Utils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class AppointmentManagementServiceIntegrationTest {

    @Autowired
    private AppointmentManagementService appointmentManagementService;

    @Autowired
    private TutorProfileService tutorProfileService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    TutorProfileRepository tutorProfileRepository;

    @Autowired
    TutorAvailabilityRepository tutorAvailabilityRepository;


    @BeforeEach
    void setUpSecurityContext() {
        List<String> roles = Collections.singletonList("ROLE_USER");
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserInfoDetails customPrincipal = new UserInfoDetails(
                "auth0UserId",
                "testuser@example.com",
                "nickname",
                "",
                authorities
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(customPrincipal, null, customPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private TutorProfile createTutorProfileWithAvailability(String tutorMail, String subjectName, double price) {
        // Create and set subject data
        SubjectData subjectData = new SubjectData();
        subjectData.setSubject(Subject.fromString(subjectName.toUpperCase()));
        subjectData.setPrice(price);

        System.out.println(subjectData);

        // Create tutor profile
        TutorProfile tutorProfile = new TutorProfile(
                List.of(subjectData),
                "TutorFirstName",
                "TutorLastName",
                "Tutor description",
                tutorMail
        );
        tutorProfile.setId(null); // ensure generated
        tutorProfileRepository.save(tutorProfile);
        String tutorId = Utils.encodeTutorProfileId(tutorProfile);

        // Get current time and calculate availability blocks
        LocalDate todayDate = LocalDate.now();
        LocalDate tomorrowDate = todayDate.plusDays(1);


        // Set tomorrow's availability for the entire day
        Timestamp tomorrowStartTime = Timestamp.valueOf(tomorrowDate.atStartOfDay());
        Timestamp tomorrowEndTime = Timestamp.valueOf(tomorrowDate.atTime(23, 0, 0));

        TutorAvailability tomorrow = new TutorAvailability();
        tomorrow.setDay(tomorrowDate);
        tomorrow.setTutor(tutorMail);
        tomorrow.setHoursBlockList(List.of(new HoursBlock(tomorrowStartTime, tomorrowEndTime)));

        tutorAvailabilityRepository.save(tomorrow);

        return tutorProfile;
    }


    /**
     * Test booking a valid appointment successfully.
     */
    @Test
    @Disabled
    public void testBookAppointment_Success() {
        // Given
        // Appointment for tomorrow
        LocalDate day = LocalDate.now().plusDays(1);
        Timestamp start = Timestamp.valueOf(day.atTime(15, 0, 0));
        Timestamp end = Timestamp.valueOf(day.atTime(16, 0, 0));

        TutorProfile tutorProfile = createTutorProfileWithAvailability( "tutor@example.com", "MATHS", 50.0);
        String tutorId = Utils.encodeTutorProfileId(tutorProfile);

        BookAppointmentRequest request = new BookAppointmentRequest(day, start, end, "MATHS", "A math lesson.");

        System.out.println(tutorAvailabilityRepository.getTutorAvailabilityByTutorAndDay(tutorProfile.getTutorMail(), day));

        ResponseEntity<BookAppointmentResponse> response = appointmentManagementService.bookAppointment(request, tutorId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    
    /**
     * Test booking an appointment when user is not authenticated.
     */
    @Test
    @Disabled
    public void testBookAppointment_Unauthorized() {
        // Clear authentication
        SecurityContextHolder.clearContext();

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setDay(LocalDate.now());
        request.setStartDate(Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
        request.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusHours(2)));
        request.setSubject("MATH");
        request.setDescription("Test Desc");

        ResponseEntity<BookAppointmentResponse> response = appointmentManagementService.bookAppointment(request, "tutorX");
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    
    /**
     * Test getUserAppointmentsByDay when no appointments exist.
     */
    @Test
    @Disabled
    public void testGetUserAppointmentsByDay_NoAppointments() {
        GetAvailabilityRequest request = new GetAvailabilityRequest();
        request.setDay(LocalDate.now());

        // Student is authenticated but has no appointments
        ResponseEntity<List<UserAppointmentResponse>> response = appointmentManagementService.getUserAppointmentsByDay(request);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().isEmpty());
    }

    /**
     * Test getUserAppointmentsByMonth with existing appointments.
     */
    @Test
    @Disabled
    public void testGetUserAppointmentsByMonth_WithData() {
        // Create a tutor and an appointment for the current student
        TutorProfile tutorProfile = createTutorProfileWithAvailability( "tutor3@example.com", "SCIENCE", 60.0);
        Appointment appointment = new Appointment(
                LocalDate.now(),
                Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)),
                "SCIENCE",
                60.0,
                "Science Lesson",
                tutorProfile,
                "testuser@example.com"
        );
        appointmentRepository.saveAndFlush(appointment);

        GetAvailabilityRequest request = new GetAvailabilityRequest();
        request.setDay(LocalDate.now());

        ResponseEntity<List<UserAppointmentResponse>> response = appointmentManagementService.getUserAppointmentsByMonth(request);
        System.out.println(response.getBody());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());
    }
}
