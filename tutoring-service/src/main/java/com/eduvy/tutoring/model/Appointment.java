package com.eduvy.tutoring.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate day;
    private Timestamp startDate;
    private Timestamp endDate;
    private String subject;
    private Double price;
    private Boolean isPaid;
    private Boolean isConfirmed;
    private Boolean isCanceled;
    private Boolean isFinished;
    private String meetingUrl; //default null, set after meeting is confirmed
    private String description;


    @ManyToOne
    @JoinColumn(name = "tutor")
    private TutorProfile tutorProfile;

    private String student;

    public Appointment(LocalDate day, Timestamp startDate, Timestamp endDate, String subject, Double price, String description, TutorProfile tutorProfile, String student) {
        this.day = day;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
        this.price = price;
        this.description = description;
        this.tutorProfile = tutorProfile;
        this.student = student;
        this.isPaid = false;
        this.isConfirmed = false;
        this.isCanceled = false;
        this.isFinished = false;
    }
}