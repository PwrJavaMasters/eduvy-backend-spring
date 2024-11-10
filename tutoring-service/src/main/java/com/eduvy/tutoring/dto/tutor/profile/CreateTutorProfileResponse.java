package com.eduvy.tutoring.dto.tutor.profile;


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
public class CreateTutorProfileResponse {

    private List<SubjectData> subjects;
    private String description;
    private String profileUrl;
}