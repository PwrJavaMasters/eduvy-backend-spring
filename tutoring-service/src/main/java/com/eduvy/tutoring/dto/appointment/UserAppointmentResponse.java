package com.eduvy.tutoring.dto.appointment;

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
public class UserAppointmentResponse {

    private LocalDate day;
    private Timestamp startDate;
    private Timestamp endDate;
    private String subject;
    private Double price;
    private Boolean isConfirmed;
    private String meetingUrl;
    private String description;
    private String tutor;
    private Boolean isPaid;
    private String paymentUrl;
}