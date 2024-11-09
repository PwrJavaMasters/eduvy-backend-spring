package com.eduvy.user.service.impl;

import com.eduvy.user.dto.tutor.get.AllSubjectsResponse;
import com.eduvy.user.dto.tutor.get.AllTutorResponse;
import com.eduvy.user.dto.tutor.get.GetTutorProfileResponse;
import com.eduvy.user.dto.tutor.get.TutorBySubjectResponse;
import com.eduvy.user.model.TutorProfile;
import com.eduvy.user.model.utils.Subject;
import com.eduvy.user.model.utils.SubjectData;
import com.eduvy.user.repository.TutorProfileRepository;
import com.eduvy.user.repository.TutorProfileReviewRepository;
import com.eduvy.user.service.TutorProfileService;
import com.eduvy.user.service.TutorsService;
import com.eduvy.user.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TutorsServiceImpl implements TutorsService {

    TutorProfileService tutorProfileService;

    TutorProfileRepository tutorProfileRepository;
    TutorProfileReviewRepository tutorProfileReviewRepository;

    @Override
    public ResponseEntity<List<AllTutorResponse>> getAllTutors() {
        List<TutorProfile> tutorProfiles = tutorProfileRepository.findAll();

        List<AllTutorResponse> tutorListingResponses = tutorProfiles.stream().map(this::mapTutorProfileToAllTutorResponse).toList();

        return ResponseEntity.ok(tutorListingResponses);
    }

    private AllTutorResponse mapTutorProfileToAllTutorResponse(TutorProfile tutorProfile) {
        return new AllTutorResponse(
                tutorProfile.getTutor().getFirstName(),
                tutorProfile.getTutor().getLastName(),
                findTutorLessonMinPrice(tutorProfile),
                findTutorLessonMaxPrice(tutorProfile),
                tutorProfileService.getReviewNumber(tutorProfile),
                tutorProfileService.getTutorProfileAvgRating(tutorProfile),
                tutorProfile.getAllSubjects(),
                Utils.encodeTutorProfileId(tutorProfile)
        );
    }

    private double findTutorLessonMinPrice(TutorProfile tutorProfile) {
        if (tutorProfile.getSubjects() == null || tutorProfile.getSubjects().isEmpty()) return 0.0;

        return tutorProfile.getSubjects().stream()
                .mapToDouble(SubjectData::getPrice)
                .min()
                .orElse(0.0);
    }

    private double findTutorLessonMaxPrice(TutorProfile tutorProfile) {
        if (tutorProfile.getSubjects() == null || tutorProfile.getSubjects().isEmpty()) return 0.0;

        return tutorProfile.getSubjects().stream()
                .mapToDouble(SubjectData::getPrice)
                .max()
                .orElse(0.0);
    }



    @Override
    public ResponseEntity<List<AllSubjectsResponse>> getAllSubjects() {
        List<TutorProfile> tutorProfiles = tutorProfileRepository.findAll();

        List<AllSubjectsResponse> responses = Arrays.stream(Subject.values())
                .map(subject -> {
                    long tutorCount = tutorProfiles.stream()
                            .filter(tutorProfile -> tutorProfile.getSubjects().stream()
                                    .anyMatch(subjectData -> subjectData.getSubject() == subject))
                            .count();
                    return new AllSubjectsResponse(subject.name(), (int) tutorCount);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<List<TutorBySubjectResponse>> getTutorsBySubjects(String subject) {
        if (subject == null) return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        final String subjectUpperCase = subject.toUpperCase();
        Subject subjectEnum = Subject.fromString(subjectUpperCase);
        if (subjectEnum == null) return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        List<TutorProfile> tutorProfiles = tutorProfileRepository.findTutorProfilesBySubject(subjectEnum);
        List<TutorBySubjectResponse> tutorListingResponses = tutorProfiles.stream().map(
                tutorProfile -> mapTutorProfileToTutorBySubjectResponse(tutorProfile, subjectUpperCase)
        ).toList();

        return ResponseEntity.ok(tutorListingResponses);
    }

    private TutorBySubjectResponse mapTutorProfileToTutorBySubjectResponse(TutorProfile tutorProfile, String subject) {
        return new TutorBySubjectResponse(
                tutorProfile.getTutor().getFirstName(),
                tutorProfile.getTutor().getLastName(),
                tutorProfile.getPriceBySubject(subject),
                tutorProfileService.getReviewNumber(tutorProfile),
                tutorProfileService.getTutorProfileAvgRating(tutorProfile),
                Utils.encodeTutorProfileId(tutorProfile)
        );
    }

    @Override
    public ResponseEntity<GetTutorProfileResponse> getTutorById(String tutorId) {
        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorId(tutorId);

        if (tutorProfile == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        GetTutorProfileResponse getTutorProfileResponse = new GetTutorProfileResponse(
                tutorProfileService.getTutorFullName(tutorProfile),
                tutorProfile.getSubjects(),
                tutorProfile.getDescription(),
                findTutorLessonMinPrice(tutorProfile),
                findTutorLessonMaxPrice(tutorProfile),
                tutorProfileService.getTutorProfileAvgRating(tutorProfile)
        );

        return ResponseEntity.ok(getTutorProfileResponse);
    }
}