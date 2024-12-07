package com.eduvy.payment.services;


import com.eduvy.payment.dto.GetPaymentUrlResponse;
import com.eduvy.payment.dto.OrderRequest;
import com.eduvy.payment.dto.PayUWebhook;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    ResponseEntity<GetPaymentUrlResponse> createOrder(OrderRequest orderRequest);

    ResponseEntity<Void> processPaymentNotify(PayUWebhook payUWebhook);
}
