package com.eduvy.payment.services;


import com.eduvy.payment.dto.OrderRequest;

public interface PaymentService {

    public String createOrder(OrderRequest orderRequest);
}
