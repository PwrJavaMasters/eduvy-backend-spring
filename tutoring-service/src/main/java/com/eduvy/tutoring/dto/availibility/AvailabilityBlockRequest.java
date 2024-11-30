package com.eduvy.tutoring.dto.availibility;

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
public class AvailabilityBlockRequest {

    private LocalDate day;
    private Timestamp startTime;
    private Timestamp endTime;
}