package com.eduvy.payment.services.impl;

import com.eduvy.payment.dto.OrderRequest;
import com.eduvy.payment.services.PayUService;
import com.eduvy.payment.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private String merchantPosId = "486591";

    private String orderCreateEndpoint = "https://secure.snd.payu.com/api/v2_1/orders";

    @Autowired
    PayUService payUService;

    public PaymentServiceImpl(PayUService payUService) {
        this.payUService = payUService;
    }

    @Override
    public String createOrder(OrderRequest orderRequest) {
        String accessToken = payUService.getAccessToken();
        System.out.println("Access Token: " + accessToken);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/json");

        Map<String, Object> payload = new HashMap<>();
        payload.put("notifyUrl", "https://eduvy.pl/api/payment/notify");
        payload.put("customerIp", "127.0.0.1");
        payload.put("merchantPosId", merchantPosId);
        payload.put("description", "Zapłata za spotkanie");
        payload.put("currencyCode", "PLN");
        payload.put("totalAmount", orderRequest.getTotalAmount());
        payload.put("extOrderId", orderRequest.getExtOrderId());

        // Dodaj informacje o produktach
        List<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Korepetycje online");
        product.put("unitPrice", orderRequest.getTotalAmount()); // Upewnij się, że to liczba
        product.put("quantity", 1); // Liczba całkowita
        products.add(product);
        payload.put("products", products);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(orderCreateEndpoint, request, Map.class);

        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
            String redirectUri = (String) response.getBody().get("redirectUri");
            return redirectUri;
        } else {
            throw new RuntimeException("Błąd podczas tworzenia zamówienia: " + response.getBody());
        }
    }


    public static void main(String[] args) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setExtOrderId("1");

        PayUService payUService = new PayUServiceImpl();
        PaymentService paymentService = new PaymentServiceImpl(payUService);
        System.out.printf(paymentService.createOrder(orderRequest));
    }

}
