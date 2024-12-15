package com.eduvy.tutoring.dto.tutor.get;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllTutorResponse {

    String firstName;
    String lastName;
    Double minPrice;
    Double maxPrice;
    Integer reviewsNumber;
    Double rating;
    List<String> subjects;
    String description;
    String tutorId;
    String imageUrl;
    Double subjectPrice;
}