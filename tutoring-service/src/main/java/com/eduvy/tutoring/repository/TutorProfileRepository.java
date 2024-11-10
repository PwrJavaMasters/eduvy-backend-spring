package com.eduvy.tutoring.repository;

import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.model.utils.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

    TutorProfile findTutorProfileByTutorMail(String tutor);

    TutorProfile findTutorProfileById(Long id);

    @Query("SELECT tp FROM TutorProfile tp JOIN tp.subjects s WHERE s.subject = :subject")
    List<TutorProfile> findTutorProfilesBySubject(@Param("subject") Subject subject);
}