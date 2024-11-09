package com.eduvy.user.service;

import com.eduvy.user.dto.tutor.profile.CreateTutorProfileRequest;
import com.eduvy.user.dto.tutor.profile.TutorProfileManagementResponse;
import org.springframework.http.ResponseEntity;

public interface TutorProfileManagementService {

    ResponseEntity<TutorProfileManagementResponse> createTutorProfile(CreateTutorProfileRequest createTutorProfileRequest);

    ResponseEntity<TutorProfileManagementResponse> getTutorProfile();

    ResponseEntity<TutorProfileManagementResponse> editTutorProfile(CreateTutorProfileRequest createTutorProfileRequest);

//    ResponseEntity<TutorProfileResponse> addSubject(AddSubjectRequest addSubjectRequest);
//
//    ResponseEntity deleteSubject(DeleteSubjectRequest deleteSubjectRequest);
//
//    ResponseEntity<EditSubjectResponse> editSubject(EditSubjectRequest editSubjectRequest);
}