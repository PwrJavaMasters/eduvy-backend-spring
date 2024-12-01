package com.eduvy.tutoring.service;

import com.eduvy.tutoring.dto.tutor.get.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TutorsService {

    ResponseEntity<List<AllTutorResponse>> getAllTutors();

    ResponseEntity<List<AllTutorResponse>> getAllTutorsFiltered(GetTutorsFilteredRequest getTutorsFilteredRequest);

    ResponseEntity<List<AllTutorResponse>> searchTutors(String phrase);

    ResponseEntity<List<AllSubjectsResponse>> getAllSubjects();

    ResponseEntity<List<TutorBySubjectResponse>> getTutorsBySubjects(String subject);

    ResponseEntity<GetTutorProfileResponse> getTutorById(String subject);
}