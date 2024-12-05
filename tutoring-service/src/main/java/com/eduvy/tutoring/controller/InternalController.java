package com.eduvy.tutoring.controller;

import com.eduvy.tutoring.dto.tutor.profile.EditUserUpdateRequest;
import com.eduvy.tutoring.service.PaymentService;
import com.eduvy.tutoring.service.TutorProfileManagementService;
import com.eduvy.tutoring.utils.ServicesURL;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal")
@AllArgsConstructor
public class InternalController {

    private final ServicesURL servicesURL;

    TutorProfileManagementService tutorProfileManagementService;
    PaymentService paymentService;

    @PostMapping("/edit-user-update")
    public ResponseEntity<Void> editUserUpdate(@RequestBody EditUserUpdateRequest editUserUpdate) {
        return tutorProfileManagementService.editUserUpdate(editUserUpdate);
    }

    @GetMapping("/payment/{appointmentId}")
    public ResponseEntity<Void> savePayment(@PathVariable("appointmentId") String appointmentId) {
        return paymentService.savePayment(appointmentId);
    }
}
