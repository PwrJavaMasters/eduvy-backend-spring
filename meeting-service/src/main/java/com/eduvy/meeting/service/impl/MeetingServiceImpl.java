package com.eduvy.meeting.service.impl;

import com.eduvy.meeting.model.Meeting;
import com.eduvy.meeting.model.dto.meeting.MeetingRequest;
import com.eduvy.meeting.model.dto.meeting.MeetingResponse;
import com.eduvy.meeting.repository.MeetingRepository;
import com.eduvy.meeting.service.MeetingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@AllArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;

    @Override
    public MeetingResponse createMeeting(MeetingRequest meetingRequest) {
        String link = generateMeetingLink(meetingRequest.getAppointmentId(),
                meetingRequest.getTutor(),
                meetingRequest.getStudent());

        Meeting meeting = Meeting.builder()
                .appointmentId(meetingRequest.getAppointmentId())
                .student(meetingRequest.getStudent())
                .tutor(meetingRequest.getTutor())
                .link(link)
                .build();

        meetingRepository.saveAndFlush(meeting);

        return new MeetingResponse(link, meeting.getAppointmentId());
    }

    @Override
    public MeetingResponse getMeetingByAppointmentId(Long appointmentId) {
        Meeting meeting = meetingRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        return new MeetingResponse(meeting.getLink(), meeting.getAppointmentId());
    }

    private String generateMeetingLink(Long appointmentId, String tutor, String student) {
        String encodedParams = Base64.getUrlEncoder()
                .encodeToString((appointmentId + "-" + tutor + "-" + student).getBytes());
        return "https://meet.eduvy.pl/" + encodedParams;
    }
}
