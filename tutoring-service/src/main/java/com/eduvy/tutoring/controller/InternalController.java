package com.eduvy.tutoring.controller;

import com.eduvy.tutoring.dto.tutor.profile.EditUserUpdateRequest;
import com.eduvy.tutoring.service.TutorProfileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal")
public class InternalController {

    @Autowired
    TutorProfileManagementService tutorProfileManagementService;

    @PostMapping("/edit-user-update")
    public ResponseEntity<Void> editUserUpdate(@RequestBody EditUserUpdateRequest editUserUpdate) {
        return tutorProfileManagementService.editUserUpdate(editUserUpdate);
    }
}
