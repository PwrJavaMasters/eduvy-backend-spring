package com.eduvy.user.service;

import com.eduvy.user.dto.tutor.get.AllSubjectsResponse;
import com.eduvy.user.dto.tutor.get.AllTutorResponse;
import com.eduvy.user.dto.tutor.get.GetTutorProfileResponse;
import com.eduvy.user.dto.tutor.get.TutorBySubjectResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TutorsService {

    ResponseEntity<List<AllTutorResponse>> getAllTutors();

    ResponseEntity<List<AllSubjectsResponse>> getAllSubjects();

    ResponseEntity<List<TutorBySubjectResponse>> getTutorsBySubjects(String subject);

    ResponseEntity<GetTutorProfileResponse> getTutorById(String subject);
}