package com.eduvy.user.dto.user.details;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {

    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Boolean isAdmin;
    private Boolean isTeacher;
    private Boolean isStudent;
    private Boolean isDeleted;

    public UserDetailsResponse(String email, String firstName, String lastName, LocalDate dateOfBirth, Boolean isAdmin, Boolean isTeacher, Boolean isStudent) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.isAdmin = isAdmin;
        this.isTeacher = isTeacher;
        this.isStudent = isStudent;
    }
}
