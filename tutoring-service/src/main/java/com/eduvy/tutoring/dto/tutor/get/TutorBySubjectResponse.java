package com.eduvy.tutoring.dto.tutor.get;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TutorBySubjectResponse {

    String firstName;
    String lastName;
    Double price;
    Integer reviewsNumber;
    Double rating;
    String tutorId;
}