package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.dto.tutor.get.*;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.model.utils.Subject;
import com.eduvy.tutoring.model.utils.SubjectData;
import com.eduvy.tutoring.repository.TutorProfileRepository;
import com.eduvy.tutoring.repository.TutorProfileReviewRepository;
import com.eduvy.tutoring.service.TutorProfileService;
import com.eduvy.tutoring.service.TutorsService;
import com.eduvy.tutoring.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.eduvy.tutoring.utils.SecurityContextHolderUtils.getCurrentUserMailFromContext;

@Service
@AllArgsConstructor
public class TutorsServiceImpl implements TutorsService {

    TutorProfileService tutorProfileService;

    TutorProfileRepository tutorProfileRepository;
    TutorProfileReviewRepository tutorProfileReviewRepository;

    @Override
    public ResponseEntity<List<AllTutorResponse>> getAllTutors() {
        List<TutorProfile> tutorProfiles = tutorProfileRepository.findAll();
        removeOwnTutorProfileFromResponse(tutorProfiles);

        List<AllTutorResponse> tutorListingResponses = tutorProfiles.stream().map(this::mapTutorProfileToAllTutorResponse).toList();

        return ResponseEntity.ok(tutorListingResponses);
    }

    @Override
    public ResponseEntity<List<AllTutorResponse>> getAllTutorsFiltered(GetTutorsFilteredRequest getTutorsFilteredRequest) {
        String subjectString = getTutorsFilteredRequest.getSubject();
        Double minPrice = getTutorsFilteredRequest.getMinPrice();
        Double maxPrice = getTutorsFilteredRequest.getMaxPrice();

        Subject subject = null;
        if (subjectString != null && !subjectString.isBlank()) {
            subject = Subject.fromString(subjectString.toUpperCase());
        }

        if (minPrice != null && minPrice == 0) {
            minPrice = null;
        }
        if (maxPrice != null && maxPrice == 0) {
            maxPrice = null;
        }

        List<TutorProfile> tutorProfiles = tutorProfileRepository.findFilteredTutors(subject, minPrice, maxPrice);
        removeOwnTutorProfileFromResponse(tutorProfiles);

        List<AllTutorResponse> tutorListingResponses = tutorProfiles.stream().map(this::mapTutorProfileToAllTutorResponse).toList();

        return ResponseEntity.ok(tutorListingResponses);
    }

    @Override
    public ResponseEntity<List<AllTutorResponse>> searchTutors(String phrase) {
        List<TutorProfile> tutorProfiles = tutorProfileRepository.searchTutorsByPhrase(phrase.trim());

        if (tutorProfiles.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<AllTutorResponse> responses = tutorProfiles.stream()
                .map(this::mapTutorProfileToAllTutorResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    private void removeOwnTutorProfileFromResponse(List<TutorProfile> tutorProfiles) {
        String userMail = getCurrentUserMailFromContext();

        TutorProfile tutorProfile = tutorProfileRepository.findTutorProfileByTutorMail(userMail);
        if (tutorProfile == null) return;

        tutorProfiles.removeIf(tp -> tp.getId().equals(tutorProfile.getId()));
    }


    private AllTutorResponse mapTutorProfileToAllTutorResponse(TutorProfile tutorProfile) {
        return new AllTutorResponse(
                tutorProfile.getFirstName(),
                tutorProfile.getLastName(),
                findTutorLessonMinPrice(tutorProfile),
                findTutorLessonMaxPrice(tutorProfile),
                tutorProfileService.getReviewNumber(tutorProfile),
                tutorProfileService.getTutorProfileAvgRating(tutorProfile),
                tutorProfile.getAllSubjects(),
                tutorProfile.getDescription(),
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
                tutorProfile.getFirstName(),
                tutorProfile.getLastName(),
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