package com.eduvy.user.dto.tutor.availability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddAvailabilityBlockRequest {

    private LocalDate day;
    private Timestamp startTime;
    private Timestamp endTime;
}