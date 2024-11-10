package com.eduvy.tutoring.dto.availibility;

import com.eduvy.tutoring.model.utils.HoursBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAvailabilityResponse {

    private LocalDate day;
    private List<HoursBlock> hoursBlockList;
}