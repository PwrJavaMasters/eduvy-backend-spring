package com.eduvy.tutoring.dto.availibility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMonthAvailabilityResponse {

    private List<GetAvailabilityResponse> appointmentsList = new ArrayList<>();
    private List<GetAvailabilityResponse> availabilityList = new ArrayList<>();
}
