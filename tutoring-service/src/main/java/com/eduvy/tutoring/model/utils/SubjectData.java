package com.eduvy.tutoring.model.utils;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
public class SubjectData {

    @Enumerated(EnumType.STRING)
    private Subject subject;
    private Double price;
}