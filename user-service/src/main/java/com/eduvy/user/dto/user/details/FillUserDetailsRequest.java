package com.eduvy.user.dto.user.details;

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
    public Boolean isTeacher;
    public Boolean isStudent;
    public Boolean isDeleted;
}
