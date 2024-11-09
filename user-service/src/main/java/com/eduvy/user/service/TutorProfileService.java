package com.eduvy.user.service;


import com.eduvy.user.model.TutorProfile;

public interface TutorProfileService {

    TutorProfile getTutorProfileByTutorId(String tutorId);

    String getTutorFullName(TutorProfile tutorProfile);

    double getTutorProfileAvgRating(TutorProfile tutorProfile);

    int getReviewNumber(TutorProfile tutorProfile);

    Double getSubjectPrice(TutorProfile tutorProfile, String subject);
}