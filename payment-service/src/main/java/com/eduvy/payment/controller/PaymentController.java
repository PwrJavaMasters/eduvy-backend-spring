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


    @GetMapping("/test")
    public String test() {
        return "Payment Service is up";
    }

    @PostMapping("/get-payment-link")
    public ResponseEntity<GetPaymentUrlResponse> makeOrder(@RequestBody OrderRequest orderRequest) {
        return paymentService.createOrder(orderRequest);
    }

    @PostMapping("/notify")
    public ResponseEntity<Void> paymentNotify(@RequestBody PayUWebhook payUWebhook) {
        return paymentService.processPaymentNotify(payUWebhook);
    }
}
