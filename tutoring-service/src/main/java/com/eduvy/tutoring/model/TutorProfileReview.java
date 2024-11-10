package com.eduvy.tutoring.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TutorProfileReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    Double rating;
    String description;
    Timestamp date;

    @ManyToOne
    TutorProfile tutorProfile;

    public TutorProfileReview(Double rating, String description, TutorProfile tutorProfile) {
        this.rating = rating;
        this.description = description;
        this.date = Timestamp.from(Instant.now());
        this.tutorProfile = tutorProfile;
    }
}