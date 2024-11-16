package com.eduvy.tutoring.model.utils;

import com.eduvy.tutoring.utils.TimestampSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

//    @JsonSerialize(using = TimestampSerializer.class)
    Timestamp startTime;

//    @JsonSerialize(using = TimestampSerializer.class)
    Timestamp endTime;
}