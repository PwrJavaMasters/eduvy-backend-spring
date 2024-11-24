package com.eduvy.payment.controller;

import com.eduvy.payment.dto.OrderRequest;
import com.eduvy.payment.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    PaymentService paymentService;


    @GetMapping("/test")
    public String test() {
        return "Payment Service is up";
    }

    @GetMapping("/make-order")
    public String makeOrder(@RequestBody OrderRequest orderRequest) {
        return paymentService.createOrder(orderRequest);
    }
}
