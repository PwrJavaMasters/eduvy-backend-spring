package com.eduvy.tutoring.service;


import com.eduvy.tutoring.model.TutorProfile;

public interface TutorProfileService {

    TutorProfile getTutorProfileByTutorId(String tutorId);

    String getTutorFullName(TutorProfile tutorProfile);

    double getTutorProfileAvgRating(TutorProfile tutorProfile);

    int getReviewNumber(TutorProfile tutorProfile);

    Double getSubjectPrice(TutorProfile tutorProfile, String subject);

    TutorProfile getTutorProfileByTutorMail(String userMail);
}