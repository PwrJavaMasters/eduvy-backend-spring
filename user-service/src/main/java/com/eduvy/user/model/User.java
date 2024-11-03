package com.eduvy.user.model;


import com.eduvy.user.controller.dto.FillUserDetailsRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; //check
    private String firstName; //check
    private String lastName; //check
    private LocalDate dateOfBirth; //check
    private Boolean isAdmin;
    private Boolean isTeacher; //check
    private Boolean isStudent; //check
    private Boolean isNewsletter; //check
    private Boolean isDeleted;

    public void setUserDetails(FillUserDetailsRequest fillUserDetailsRequest) {
        firstName = fillUserDetailsRequest.firstName;
        lastName = fillUserDetailsRequest.lastName;
        dateOfBirth = fillUserDetailsRequest.dateOfBirth; // todo check format
        isTeacher = fillUserDetailsRequest.isTeacher;
        isStudent = fillUserDetailsRequest.isStudent;
        isNewsletter = fillUserDetailsRequest.isNewsletter;
    }
}
