package com.eduvy.user.repository;

import com.eduvy.user.model.TutorProfile;
import com.eduvy.user.model.TutorProfileReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorProfileReviewRepository extends JpaRepository<TutorProfileReview, Long> {

    @Query("SELECT AVG(tpr.rating) FROM TutorProfileReview tpr WHERE tpr.tutorProfile = :tutorProfile")
    Double findAvgRatingByTutorProfile(@Param("tutorProfile") TutorProfile tutorProfile);

    @Query("SELECT COUNT(tpr.id) FROM TutorProfileReview tpr WHERE tpr.tutorProfile = :tutorProfile")
    Integer findReviewNumberByTutorProfile(@Param("tutorProfile") TutorProfile tutorProfile);
}