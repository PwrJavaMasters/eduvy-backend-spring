package com.eduvy.payment.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AppointmentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime paymentDate;
    private Double amount;
    private String appointment;
    private String status;

    public AppointmentPayment(LocalDateTime paymentDate, Double amount, String appointment, String status) {
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.appointment = appointment;
        this.status = status;
    }
}
