package com.eduvy.payment.controller;

import com.eduvy.payment.dto.GetPaymentUrlResponse;
import com.eduvy.payment.dto.OrderRequest;
import com.eduvy.payment.dto.PayUWebhook;
import com.eduvy.payment.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    PaymentService paymentService;

    @PostMapping("/notify")
    public ResponseEntity<Void> paymentNotify(@RequestBody PayUWebhook payUWebhook) {
        return paymentService.processPaymentNotify(payUWebhook);
    }
}
