package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String getPaymentUrl(String request) {
        return "chuju zapłać";
    }
}
