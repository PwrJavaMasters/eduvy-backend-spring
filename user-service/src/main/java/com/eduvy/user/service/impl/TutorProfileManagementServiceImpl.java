package com.eduvy.user.service.impl;


import com.eduvy.user.dto.tutor.profile.CreateTutorProfileRequest;
import com.eduvy.user.dto.tutor.profile.TutorProfileManagementResponse;
import com.eduvy.user.model.TutorProfile;
import com.eduvy.user.model.UserDetails;
import com.eduvy.user.repository.TutorProfileRepository;
import com.eduvy.user.service.TutorProfileManagementService;
import com.eduvy.user.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TutorProfileManagementServiceImpl implements TutorProfileManagementService {

    Environment environment;

    UserServiceImpl userService;

    TutorProfileRepository tutorProfileRepository;


    @Override
    @Transactional
    public ResponseEntity<TutorProfileManagementResponse> createTutorProfile(CreateTutorProfileRequest createTutorProfileRequest) {
        UserDetails tutor = userService.getUserFromContext();
        if (tutor == null) return
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (tutorProfileRepository.findTutorProfileByTutor(tutor) != null){
            System.out.println("chuj juz istenieje");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (createTutorProfileRequest == null)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        //todo create validation
        TutorProfile tutorProfile = new TutorProfile(
                createTutorProfileRequest.getSubjects(),
                createTutorProfileRequest.getDescription(),
                tutor
        );

        tutorProfileRepository.save(tutorProfile);

        TutorProfileManagementResponse response = new TutorProfileManagementResponse(
                tutorProfile.getSubjects(),
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
        UserDetails tutor = userService.getUserFromContext();
        TutorProfile tutorProfile = tutorProfileRepository.findTutorProfileByTutor(tutor);

        if (tutorProfile == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        TutorProfileManagementResponse tutorProfileResponse = new TutorProfileManagementResponse(
          tutorProfile.getSubjects(),
          tutorProfile.getDescription(),
          createProfileUrl(tutorProfile)
        );

        return ResponseEntity.ok(tutorProfileResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<TutorProfileManagementResponse> editTutorProfile(CreateTutorProfileRequest createTutorProfileRequest) {
        UserDetails tutor = userService.getUserFromContext();

        if (tutor == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (createTutorProfileRequest == null)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorProfile existingTutorProfile = tutorProfileRepository.findTutorProfileByTutor(tutor);
        if (existingTutorProfile == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        existingTutorProfile.setSubjects(createTutorProfileRequest.getSubjects());
        existingTutorProfile.setDescription(createTutorProfileRequest.getDescription());
        tutorProfileRepository.save(existingTutorProfile);

        TutorProfileManagementResponse response = new TutorProfileManagementResponse(
                existingTutorProfile.getSubjects(),
                existingTutorProfile.getDescription(),
                createProfileUrl(existingTutorProfile)
        );

        return ResponseEntity.ok(response);
    }
}