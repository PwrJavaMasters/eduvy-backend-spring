package com.eduvy.user.dto.tutor.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllSubjectsResponse {

    private String subjects;
    private Integer tutorCount;
}