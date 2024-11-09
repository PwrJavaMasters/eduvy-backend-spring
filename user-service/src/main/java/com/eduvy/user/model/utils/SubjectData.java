package com.eduvy.user.model.utils;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SubjectData {

    @Enumerated(EnumType.STRING)
    private Subject subject;
    private Double price;
}