package com.eduvy.tutoring.repository;

import com.eduvy.tutoring.model.TutorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, Long> {

    TutorAvailability getTutorAvailabilityByDay(LocalDate day);

    TutorAvailability getTutorAvailabilityByTutorAndDay(String tutor, LocalDate day);

    @Query("SELECT ta FROM TutorAvailability ta WHERE ta.tutor = :tutor AND ta.day BETWEEN :startDate AND :endDate")
    List<TutorAvailability> findTutorAvailabilityByMonth(@Param("tutor") String tutor, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}