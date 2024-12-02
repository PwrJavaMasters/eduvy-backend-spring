package com.eduvy.meeting.controller;

import com.eduvy.meeting.model.dto.meeting.MeetingRequest;
import com.eduvy.meeting.model.dto.meeting.MeetingResponse;
import com.eduvy.meeting.service.MeetingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/internal")
@RestController
@AllArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/link")
    public ResponseEntity<MeetingResponse> createMeeting(@RequestBody MeetingRequest meetingRequest) {
        MeetingResponse meetingResponse = meetingService.createMeeting(meetingRequest);
        return ResponseEntity.ok(meetingResponse);
    }

    @GetMapping("/link")
    public ResponseEntity<MeetingResponse> getMeetingLink(@RequestParam("appointmentId") Long appointmentId) {
        MeetingResponse meetingResponse = meetingService.getMeetingByAppointmentId(appointmentId);
        return ResponseEntity.ok(meetingResponse);
    }
}
