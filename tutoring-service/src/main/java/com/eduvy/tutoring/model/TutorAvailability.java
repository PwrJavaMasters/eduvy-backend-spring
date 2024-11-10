package com.eduvy.tutoring.model;


import com.eduvy.tutoring.model.utils.HoursBlock;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TutorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private LocalDate day;

    @ElementCollection
    private List<HoursBlock> hoursBlockList;

    private String tutor;

    public TutorAvailability(LocalDate day, String tutor) {
        this.day = day;
        this.tutor = tutor;
        this.hoursBlockList = new ArrayList<>();
    }
}