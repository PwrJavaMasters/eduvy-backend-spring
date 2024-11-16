package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.dto.user.UserDetails;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.model.utils.Subject;
import com.eduvy.tutoring.model.utils.SubjectData;
import com.eduvy.tutoring.repository.TutorProfileRepository;
import com.eduvy.tutoring.repository.TutorProfileReviewRepository;
import com.eduvy.tutoring.service.TutorProfileService;
import com.eduvy.tutoring.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TutorProfileServiceImpl implements TutorProfileService {

    TutorProfileRepository tutorProfileRepository;
    TutorProfileReviewRepository tutorProfileReviewRepository;

    @Override
    public TutorProfile getTutorProfileByTutorId(String tutorId) {
        return tutorProfileRepository.findTutorProfileById(Utils.decodeTutorProfileId(tutorId));
    }

    @Override
    public String getTutorFullName(TutorProfile tutorProfile) {
        return tutorProfile.getFirstName() + " " + tutorProfile.getLastName();
    }

    public double getTutorProfileAvgRating(TutorProfile tutorProfile) {
        Double avgRating = tutorProfileReviewRepository.findAvgRatingByTutorProfile(tutorProfile);
        return avgRating == null ? 0.0 : avgRating;
    }

    public int getReviewNumber(TutorProfile tutorProfile) {
        Integer reviewNumber = tutorProfileReviewRepository.findReviewNumberByTutorProfile(tutorProfile);
        return reviewNumber == null ? 0 : reviewNumber;
    }

    @Override
    public Double getSubjectPrice(TutorProfile tutorProfile, String subject) {
        if (tutorProfile == null || subject == null) {
            return null;
        }

        Subject searchSubject = Subject.fromString(subject);
        if (searchSubject == null) {
            return null;
        }

        return tutorProfile.getSubjects().stream()
                .filter(subjectData -> subjectData.getSubject() == searchSubject)
                .findFirst()
                .map(SubjectData::getPrice)
                .orElse(null);
    }

    @Override
    public TutorProfile getTutorProfileByTutorMail(String userMail) {
        return tutorProfileRepository.findTutorProfileByTutorMail(userMail);
    }
}