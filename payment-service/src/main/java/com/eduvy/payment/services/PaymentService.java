package com.eduvy.payment.services;


import com.eduvy.payment.dto.OrderRequest;
import com.eduvy.payment.dto.PayUWebhook;

public interface PaymentService {

    public String createOrder(OrderRequest orderRequest);

    String processPaymentNotify(PayUWebhook payUWebhook);
}
