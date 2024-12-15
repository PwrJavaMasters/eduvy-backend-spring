package com.eduvy.tutoring.controller;


import com.eduvy.tutoring.dto.student.StudentHomePageResponse;
import com.eduvy.tutoring.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("tutoring/student")
public class StudentController {

    StudentService studentService;

    @GetMapping("home-page")
    public ResponseEntity<StudentHomePageResponse> getStudentHomePageResponse() {
        return studentService.getStudentHomePageResponse();
    }
}
