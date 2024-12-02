package com.eduvy.payment.repository;

import com.eduvy.payment.model.AppointmentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentPaymentRepository  extends JpaRepository<AppointmentPayment, Long> {
}
