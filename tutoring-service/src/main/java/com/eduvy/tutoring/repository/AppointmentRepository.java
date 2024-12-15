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

    Appointment findAppointmentById(Long id);

    List<Appointment> findAppointmentsByStudentAndDay(String student, LocalDate day);

    List<Appointment> findAppointmentsByTutorProfileAndDay(TutorProfile tutorProfile, LocalDate day);

    @Query("SELECT a FROM Appointment a WHERE a.student = :student AND a.day BETWEEN :startOfMonth AND :endOfMonth")
    List<Appointment> findAppointmentsByStudentAndMonth(@Param("student") String student,
                                                        @Param("startOfMonth") LocalDate startOfMonth,
                                                        @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT a FROM Appointment a WHERE a.tutorProfile = :tutorProfile AND a.day BETWEEN :startOfMonth AND :endOfMonth")
    List<Appointment> findAppointmentsByTutorProfileAndMonth(@Param("tutorProfile") TutorProfile tutorProfile,
                                                             @Param("startOfMonth") LocalDate startOfMonth,
                                                             @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.student = :student AND a.day BETWEEN :startOfMonth AND :endOfMonth")
    int countLessonsScheduledInCurrentMonth(@Param("student") String student,
                                            @Param("startOfMonth") LocalDate startOfMonth,
                                            @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.student = :student AND a.isFinished = true")
    int countLessonsCompleted(@Param("student") String student);

    @Query("SELECT COUNT(DISTINCT a.subject) FROM Appointment a WHERE a.student = :student")
    int countDistinctSubjects(@Param("student") String student);

    // Count lessons scheduled for this month
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.tutorProfile = :tutorProfile AND a.day BETWEEN :startOfMonth AND :endOfMonth")
    int countLessonsScheduledForTutorInCurrentMonth(@Param("tutorProfile") TutorProfile tutorProfile,
                                                    @Param("startOfMonth") LocalDate startOfMonth,
                                                    @Param("endOfMonth") LocalDate endOfMonth);

    // Count completed lessons for a tutor
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.tutorProfile = :tutorProfile AND a.isFinished = true")
    int countCompletedLessonsForTutor(@Param("tutorProfile") TutorProfile tutorProfile);

    // Calculate total money earned from completed lessons
    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Appointment a WHERE a.tutorProfile = :tutorProfile AND a.isFinished = true")
    double calculateMoneyEarnedForTutor(@Param("tutorProfile") TutorProfile tutorProfile);
}
