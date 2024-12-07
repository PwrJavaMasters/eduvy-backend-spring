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
@RequestMapping("/internal")
public class InternalController {

    PaymentService paymentService;


    @PostMapping("/get-payment-link")
    public ResponseEntity<GetPaymentUrlResponse> makeOrder(@RequestBody OrderRequest orderRequest) {
        return paymentService.createOrder(orderRequest);
    }
}

