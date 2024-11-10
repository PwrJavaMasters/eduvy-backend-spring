package com.eduvy.tutoring.service;


import com.eduvy.tutoring.model.Appointment;

public interface AppointmentService {

    Appointment getAppointmentByEncodedId(String id);

    void savePayment(Appointment appointment);
}