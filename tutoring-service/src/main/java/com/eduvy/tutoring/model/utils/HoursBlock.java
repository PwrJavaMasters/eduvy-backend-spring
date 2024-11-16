package com.eduvy.tutoring.model.utils;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.sql.Timestamp;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HoursBlock {

    Timestamp startTime;
    Timestamp endTime;
}