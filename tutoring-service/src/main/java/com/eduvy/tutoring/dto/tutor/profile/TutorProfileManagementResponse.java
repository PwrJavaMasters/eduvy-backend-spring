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
public class TutorProfileManagementResponse {

    private List<SubjectData> subjects;
    private String firstName;
    private String lastName;
    private String description;
    private String profileUrl;
}