package com.eduvy.tutoring.repository;

import com.eduvy.tutoring.model.TutorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, Long> {

    TutorAvailability getTutorAvailabilityByDay(LocalDate day);

    TutorAvailability getTutorAvailabilityByTutorAndDay(String tutor, LocalDate day);
}