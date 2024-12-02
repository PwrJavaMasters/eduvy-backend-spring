package com.eduvy.tutoring.dto.meeting;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MeetingRequest {
    private Long appointmentId;
    private String student;
    private String tutor;
}
