package com.eduvy.user.controller.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
public class FillUserDetailsRequest {

    public String email;
    public String firstName;
    public String lastName;
    public LocalDate dateOfBirth;
    public Boolean isAdmin;
    public Boolean isTeacher;
    public Boolean isStudent;
    public Boolean isNewsletter;
    public Boolean isDeleted;
}
