package com.eduvy.tutoring.dto.tutor.get;

import com.eduvy.tutoring.model.utils.SubjectData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetTutorProfileResponse {

    private String name;
    private List<SubjectData> subjects;
    private String description;
    private Double minPrice;
    private Double maxPrice;
    private Double rating;
    private String imageUrl;
    private String email;
}