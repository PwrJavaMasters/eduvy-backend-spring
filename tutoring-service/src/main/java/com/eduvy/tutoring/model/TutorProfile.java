package com.eduvy.tutoring.model;

import com.eduvy.tutoring.model.utils.SubjectData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TutorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<SubjectData> subjects;

    private String firstName;
    private String lastName;

    @Lob
    private String description;

    private String tutorMail;

    public TutorProfile(List<SubjectData> subjects, String firstName, String lastName, String description, String tutorMail) {
        this.subjects = subjects;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.tutorMail = tutorMail;
    }

    public Double getPriceBySubject(String subject) {
        return subjects.stream()
                .filter(subjectData -> subjectData.getSubject().name().equalsIgnoreCase(subject))
                .findFirst()
                .map(SubjectData::getPrice)
                .orElse(0.0); //todo check if it is okay to send 0
    }

    public List<String> getAllSubjects() {
        return subjects.stream()
                .map(subjectData -> subjectData.getSubject().name())
                .collect(Collectors.toList());
    }
}