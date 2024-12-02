package com.eduvy.meeting.model.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingResponse {
    private String link;
    private Long appointmentId;
}
