package com.eduvy.tutoring.dto.tutor;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TutorHomePageResponse {

    private int lessonsScheduled; //this month
    private int lessonsCompleted;
    private double moneyAmount;
}
