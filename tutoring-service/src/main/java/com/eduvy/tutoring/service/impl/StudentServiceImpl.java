package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.dto.student.StudentHomePageResponse;
import com.eduvy.tutoring.dto.user.UserDetails;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.eduvy.tutoring.utils.SecurityContextHolderUtils.getCurrentUserMailFromContext;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    AppointmentRepository appointmentRepository;

    @Override
    public ResponseEntity<StudentHomePageResponse> getStudentHomePageResponse() {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        int lessonsScheduled = appointmentRepository.countLessonsScheduledInCurrentMonth(userMail, startOfMonth, endOfMonth);
        int lessonsCompleted = appointmentRepository.countLessonsCompleted(userMail);
        int subjectsAmount = appointmentRepository.countDistinctSubjects(userMail);

        StudentHomePageResponse studentHomePageResponse = new StudentHomePageResponse(lessonsScheduled, lessonsCompleted, subjectsAmount);

        return ResponseEntity.ok(studentHomePageResponse);
    }
}
