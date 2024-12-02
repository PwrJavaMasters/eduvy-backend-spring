package com.eduvy.meeting.model.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRequest {
    private Long appointmentId;
    private String student;
    private String tutor;
}
