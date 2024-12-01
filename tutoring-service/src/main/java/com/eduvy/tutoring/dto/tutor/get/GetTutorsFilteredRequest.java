package com.eduvy.tutoring.dto.tutor.get;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetTutorsFilteredRequest {

    private String subject;
    private Double minPrice;
    private Double maxPrice;
}
