package com.eduvy.tutoring.service;


import com.eduvy.tutoring.model.Appointment;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    String getPaymentUrl(Appointment appointment);

    ResponseEntity<Void> savePayment(String appointmentId);
}