package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.config.security.UserInfoDetails;
import com.eduvy.tutoring.dto.availibility.AvailabilityBlockRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityResponse;
import com.eduvy.tutoring.model.TutorAvailability;
import com.eduvy.tutoring.model.utils.HoursBlock;
import com.eduvy.tutoring.repository.TutorAvailabilityRepository;
import com.eduvy.tutoring.service.TutorAvailabilityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class TutorAvailabilityServiceIntegrationTest {

    @Autowired
    private TutorAvailabilityService tutorAvailabilityService;

    @Autowired
    private TutorAvailabilityRepository tutorAvailabilityRepository;

    private LocalDate testDay;
    private Timestamp startTime;
    private Timestamp endTime;

    @BeforeEach
    public void setUp() {
        testDay = LocalDate.now();

//        BLOCK -> 10:00 - 12:00
        startTime = Timestamp.valueOf(LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0));
        endTime = Timestamp.valueOf(LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0));
    }

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


    @Test
    @Disabled
    public void testAddAvailabilityBlock_NewBlock() {
        // Given a request for a new availability block
        AvailabilityBlockRequest request = new AvailabilityBlockRequest();
        request.setDay(testDay);
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        // When we add the block
        ResponseEntity<GetAvailabilityResponse> response = tutorAvailabilityService.addAvailabilityBlock(request);

        // Then the response should be OK
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // Check the body
        GetAvailabilityResponse responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(testDay, responseBody.getDay());

        // We expect exactly one block saved
        Assertions.assertNotNull(responseBody.getHoursBlockList());
        Assertions.assertEquals(1, responseBody.getHoursBlockList().size());

        HoursBlock returnedBlock = responseBody.getHoursBlockList().get(0);
        Assertions.assertEquals(startTime, returnedBlock.getStartTime());
        Assertions.assertEquals(endTime, returnedBlock.getEndTime());

        // Verify that the data is actually stored in DB
        TutorAvailability savedAvailability = tutorAvailabilityRepository.getTutorAvailabilityByTutorAndDay("testuser@example.com", testDay);
        Assertions.assertNotNull(savedAvailability);
        Assertions.assertEquals(1, savedAvailability.getHoursBlockList().size());
        Assertions.assertEquals(startTime, savedAvailability.getHoursBlockList().get(0).getStartTime());
        Assertions.assertEquals(endTime, savedAvailability.getHoursBlockList().get(0).getEndTime());
    }

    @Test
    @Disabled
    public void testAddAvailabilityBlock_MergingExistingBlocks() {
        LocalDateTime baseTime = LocalDateTime.now().withSecond(0).withNano(0);

        // Existing block: 09:00 - 10:30
        Timestamp existingStart = Timestamp.valueOf(baseTime.withHour(9).withMinute(0));
        Timestamp existingEnd = Timestamp.valueOf(baseTime.withHour(10).withMinute(30));

        TutorAvailability existingAvailability = new TutorAvailability();
        existingAvailability.setTutor("testuser@example.com");
        existingAvailability.setDay(testDay);
        existingAvailability.setHoursBlockList(List.of(new HoursBlock(existingStart, existingEnd)));
        tutorAvailabilityRepository.saveAndFlush(existingAvailability);

        // New block that overlaps: 10:00 - 12:00
        Timestamp requestStartTime = Timestamp.valueOf(baseTime.withHour(10).withMinute(0));
        Timestamp requestEndTime = Timestamp.valueOf(baseTime.withHour(12).withMinute(0));

        AvailabilityBlockRequest request = new AvailabilityBlockRequest();
        request.setDay(testDay);
        request.setStartTime(requestStartTime);
        request.setEndTime(requestEndTime);

        // When we add the block
        ResponseEntity<GetAvailabilityResponse> response = tutorAvailabilityService.addAvailabilityBlock(request);

        // Then the response should be OK and we should get a merged block
        Assertions.assertEquals(200, response.getStatusCodeValue());

        GetAvailabilityResponse responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);

        // After merging (9:00 - 10:30) and (10:00 - 12:00), the resulting block should be 9:00 - 12:00
        Assertions.assertEquals(1, responseBody.getHoursBlockList().size());
        HoursBlock mergedBlock = responseBody.getHoursBlockList().get(0);
        System.out.println("mergedBlock.getStartTime(): " + mergedBlock.getStartTime());
        System.out.println("existingStart: " + existingStart);

        // Use assertEquals instead of assertTrue(…equals(…)) for clarity
        Assertions.assertEquals(existingStart, mergedBlock.getStartTime());
        Assertions.assertEquals(requestEndTime, mergedBlock.getEndTime());

        // Check DB
        TutorAvailability savedAvailability = tutorAvailabilityRepository.getTutorAvailabilityByTutorAndDay("testuser@example.com", testDay);
        Assertions.assertNotNull(savedAvailability);
        Assertions.assertEquals(1, savedAvailability.getHoursBlockList().size());
        Assertions.assertEquals(existingStart, savedAvailability.getHoursBlockList().get(0).getStartTime());
        Assertions.assertEquals(requestEndTime, savedAvailability.getHoursBlockList().get(0).getEndTime());
    }


    @Test
    @Disabled
    public void testAddAvailabilityBlock_InvalidBlock() {
        // Invalid scenario: The start and end times do not match the requested day
        AvailabilityBlockRequest request = new AvailabilityBlockRequest();
        request.setDay(LocalDate.now());
        // Set a start time that is on a different day
        request.setStartTime(Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(10)));
        request.setEndTime(Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(11)));

        ResponseEntity<GetAvailabilityResponse> response = tutorAvailabilityService.addAvailabilityBlock(request);

        // Should return 422 Unprocessable Entity
        Assertions.assertEquals(422, response.getStatusCodeValue());
    }
}
