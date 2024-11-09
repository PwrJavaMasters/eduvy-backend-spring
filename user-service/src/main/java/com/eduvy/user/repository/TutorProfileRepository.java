package com.eduvy.user.repository;

import com.eduvy.user.model.TutorProfile;
import com.eduvy.user.model.UserDetails;
import com.eduvy.user.model.utils.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

    TutorProfile findTutorProfileByTutor(UserDetails tutor);

    TutorProfile findTutorProfileById(Long id);

    @Query("SELECT tp FROM TutorProfile tp JOIN tp.subjects s WHERE s.subject = :subject")
    List<TutorProfile> findTutorProfilesBySubject(@Param("subject") Subject subject);
}