package com.eduvy.meeting.service;


import com.eduvy.meeting.model.dto.meeting.MeetingRequest;
import com.eduvy.meeting.model.dto.meeting.MeetingResponse;

public interface MeetingService {
    MeetingResponse createMeeting(MeetingRequest meetingRequest);
    MeetingResponse getMeetingByAppointmentId(Long appointmentId);
}
