package com.eduvy.tutoring.service;


import com.eduvy.tutoring.dto.student.StudentHomePageResponse;
import org.springframework.http.ResponseEntity;

public interface StudentService {

    public ResponseEntity<StudentHomePageResponse> getStudentHomePageResponse();
}
