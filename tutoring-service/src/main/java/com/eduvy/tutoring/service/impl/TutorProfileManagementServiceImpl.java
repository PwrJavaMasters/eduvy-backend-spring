package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.dto.tutor.profile.CreateTutorProfileRequest;
import com.eduvy.tutoring.dto.tutor.profile.EditUserUpdateRequest;
import com.eduvy.tutoring.dto.tutor.profile.TutorProfileManagementResponse;
import com.eduvy.tutoring.dto.user.UserDetails;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.repository.TutorProfileRepository;
import com.eduvy.tutoring.service.TutorProfileManagementService;
import com.eduvy.tutoring.service.UserService;
import com.eduvy.tutoring.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.eduvy.tutoring.utils.SecurityContextHolderUtils.getCurrentUserMailFromContext;

@Service
@AllArgsConstructor
public class TutorProfileManagementServiceImpl implements TutorProfileManagementService {

    Environment environment;

    UserService userService;

    TutorProfileRepository tutorProfileRepository;


    @Override
    @Transactional
    public ResponseEntity<TutorProfileManagementResponse> createTutorProfile(CreateTutorProfileRequest createTutorProfileRequest) {
        UserDetails tutor = userService.getUserDetails();
        if (tutor == null) {
            System.out.println("Failed to find user.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (tutorProfileRepository.findTutorProfileByTutorMail(tutor.getEmail()) != null) {
            System.out.println("Tutor profile already exists.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (createTutorProfileRequest == null) {
            System.out.println("Null request.");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        //todo create validation
        TutorProfile tutorProfile = new TutorProfile(
                createTutorProfileRequest.getSubjects(),
                tutor.getFirstName(),
                tutor.getLastName(),
                createTutorProfileRequest.getDescription(),
                tutor.getEmail()
        );

        tutorProfileRepository.save(tutorProfile);

        TutorProfileManagementResponse response = new TutorProfileManagementResponse(
                tutorProfile.getSubjects(),
                tutorProfile.getFirstName(),
                tutorProfile.getLastName(),
                tutorProfile.getDescription(),
                createProfileUrl(tutorProfile)
        );

        return ResponseEntity.ok(response);
    }

    private String createProfileUrl(TutorProfile tutorProfile) {
        return environment.getProperty("frontend.server") + "/tutors/" + Utils.encodeTutorProfileId(tutorProfile);
    }

    @Override
    @Transactional
    public ResponseEntity<TutorProfileManagementResponse> getTutorProfile() {
        String tutor = getCurrentUserMailFromContext();
        TutorProfile tutorProfile = tutorProfileRepository.findTutorProfileByTutorMail(tutor);

        if (tutorProfile == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        TutorProfileManagementResponse tutorProfileResponse = new TutorProfileManagementResponse(
                tutorProfile.getSubjects(),
                tutorProfile.getFirstName(),
                tutorProfile.getLastName(),
                tutorProfile.getDescription(),
                createProfileUrl(tutorProfile)
        );

        return ResponseEntity.ok(tutorProfileResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<TutorProfileManagementResponse> editTutorProfile(CreateTutorProfileRequest createTutorProfileRequest) {
        String tutorMail = getCurrentUserMailFromContext();

        if (tutorMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (createTutorProfileRequest == null)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorProfile existingTutorProfile = tutorProfileRepository.findTutorProfileByTutorMail(tutorMail);
        if (existingTutorProfile == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        existingTutorProfile.setSubjects(createTutorProfileRequest.getSubjects());
        existingTutorProfile.setDescription(createTutorProfileRequest.getDescription());
        tutorProfileRepository.save(existingTutorProfile);

        TutorProfileManagementResponse response = new TutorProfileManagementResponse(
                existingTutorProfile.getSubjects(),
                existingTutorProfile.getFirstName(),
                existingTutorProfile.getLastName(),
                existingTutorProfile.getDescription(),
                createProfileUrl(existingTutorProfile)
        );

        return ResponseEntity.ok(response);
    }


    @Override
    @Transactional
    public ResponseEntity<Void> editUserUpdate(EditUserUpdateRequest editUserUpdateRequest) {
        TutorProfile tutorProfile = tutorProfileRepository.findTutorProfileByTutorMail(editUserUpdateRequest.getEmail());
        if (tutorProfile == null) {
            return ResponseEntity.ok().build();
        }

        tutorProfile.setFirstName(editUserUpdateRequest.getFirstName());
        tutorProfile.setFirstName(editUserUpdateRequest.getFirstName());

        tutorProfileRepository.save(tutorProfile);

        return ResponseEntity.ok().build();
    }
}