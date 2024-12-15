package com.eduvy.tutoring.dto.student;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentHomePageResponse {

    private int lessonsScheduled; //today's lesson count
    private int lessonsCompleted; //total lesson completed
    private int subjectsAmount; //distinct subject count
}
