package com.eduvy.user.model;

import com.eduvy.user.model.utils.SubjectData;
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

    private String description;

    @OneToOne
    @JoinColumn(name = "tutor")
    private UserDetails tutor;

    public TutorProfile(List<SubjectData> subjects, String description, UserDetails tutor) {
        this.subjects = subjects;
        this.description = description;
        this.tutor = tutor;
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