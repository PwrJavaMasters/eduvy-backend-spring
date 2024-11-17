package com.eduvy.tutoring.repository;


import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.model.TutorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAppointmentsByStudentAndDay(String student, LocalDate day);

    List<Appointment> findAppointmentsByTutorProfileAndDay(TutorProfile tutorProfile, LocalDate day);

    @Query("SELECT a FROM Appointment a WHERE a.student = :student AND a.day BETWEEN :startOfMonth AND :endOfMonth")
    List<Appointment> findAppointmentsByStudentAndMonth(@Param("student") String student,
                                                        @Param("startOfMonth") LocalDate startOfMonth,
                                                        @Param("endOfMonth") LocalDate endOfMonth);
}