package com.eduvy.tutoring.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {

    private String email; //check
    private String firstName; //check
    private String lastName; //check
    private LocalDate dateOfBirth; //check
    private Boolean isAdmin;
    private Boolean isTeacher; //check
    private Boolean isStudent; //check
    private Boolean isNewsletter; //check
    private Boolean isDeleted;
}
