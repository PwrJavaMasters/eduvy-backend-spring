package com.eduvy.tutoring.controller;

import com.eduvy.tutoring.dto.tutor.profile.EditUserUpdateRequest;
import com.eduvy.tutoring.service.TutorProfileManagementService;
import com.eduvy.tutoring.utils.ServicesURL;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal")
@AllArgsConstructor
public class InternalController {

    private final ServicesURL servicesURL;

    @Autowired
    TutorProfileManagementService tutorProfileManagementService;

    @PostMapping("/edit-user-update")
    public ResponseEntity<Void> editUserUpdate(@RequestBody EditUserUpdateRequest editUserUpdate) {
        return tutorProfileManagementService.editUserUpdate(editUserUpdate);
    }

}
