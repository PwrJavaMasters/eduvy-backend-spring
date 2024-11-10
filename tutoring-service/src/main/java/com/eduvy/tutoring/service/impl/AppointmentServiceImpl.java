package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.service.AppointmentService;
import com.eduvy.tutoring.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    AppointmentRepository appointmentRepository;

    @Override
    public Appointment getAppointmentByEncodedId(String id) {
        return appointmentRepository.findById(Utils.decodeAppointmentId(id)).orElse(null);
    }

    @Override
    @Transactional
    public void savePayment(Appointment appointment) {
        Appointment appointmentToSave = appointmentRepository.findById(appointment.getId()).orElse(null);
        if (appointmentToSave == null) {
            System.err.print("Failed to find paid appointment. Student: " + appointment.getStudent());
            return;
        }
        appointmentToSave.setIsPaid(true);
        appointmentRepository.save(appointmentToSave);
    }
}