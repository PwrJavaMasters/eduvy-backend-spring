package com.eduvy.user.dto.user.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
public class EditUserDetailsRequest {

    public String firstName;
    public String lastName;
}
