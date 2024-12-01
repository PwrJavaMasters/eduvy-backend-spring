package com.eduvy.user.dto.tutor.profile;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditUserUpdateRequest {

    private String email;
    private String firstName;
    private String lastName;
}
