package com.eduvy.payment.services.impl;

import com.eduvy.payment.dto.GetPaymentUrlResponse;
import com.eduvy.payment.dto.OrderRequest;
import com.eduvy.payment.dto.PayUCreateOrderResponse;
import com.eduvy.payment.dto.PayUWebhook;
import com.eduvy.payment.model.AppointmentPayment;
import com.eduvy.payment.repository.AppointmentPaymentRepository;
import com.eduvy.payment.services.PayUService;
import com.eduvy.payment.services.PaymentService;
import com.eduvy.payment.utils.ServicesURL;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private String merchantPosId = "486591";
    private String orderCreateEndpoint = "https://secure.snd.payu.com/api/v2_1/orders";

    @Autowired
    PayUService payUService;

    @Autowired
    private AppointmentPaymentRepository appointmentPaymentRepository;

    @Autowired
    ServicesURL servicesURL;

    public PaymentServiceImpl(PayUService payUService) {
        this.payUService = payUService;
    }

    CloseableHttpClient httpClient = HttpClients.createDefault();
    Gson gson = new Gson();

    @Override
    public ResponseEntity<GetPaymentUrlResponse> createOrder(OrderRequest orderRequest) {
        String accessToken = payUService.getAccessToken();
        System.out.println("Access Token: " + accessToken);

        int amount = (int) (orderRequest.getTotalAmount() * 100); //konwersja do groszy

        try {
            // Prepare the HTTP POST request
            HttpPost httpPost = new HttpPost(orderCreateEndpoint);
            httpPost.setHeader("Authorization", "Bearer " + accessToken);
            httpPost.setHeader("Content-Type", "application/json");

            // Prepare payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("notifyUrl", "https://eduvy.pl/api/payment/notify");
            payload.put("continueUrl", "https://eduvy.pl/payment-success");
            payload.put("customerIp", "127.0.0.1");
            payload.put("merchantPosId", merchantPosId);
            payload.put("description", "Payment for tutoring.");
            payload.put("currencyCode", "PLN");
            payload.put("totalAmount", amount);
            payload.put("extOrderId", orderRequest.getExtOrderId());

            // Add product details
            List<Map<String, Object>> products = new ArrayList<>();
            Map<String, Object> product = new HashMap<>();
            product.put("name", "Online tutoring");
            product.put("unitPrice", amount);
            product.put("quantity", 1);
            products.add(product);
            payload.put("products", products);

            // Set JSON payload
            String jsonPayload = gson.toJson(payload);
            StringEntity entity = new StringEntity(jsonPayload);
            httpPost.setEntity(entity);

            // Execute request and process response
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                System.out.println("status code: " +statusCode);
                System.out.println("body:" + responseBody);

                if (statusCode != 200 && statusCode != 302) {
                    return null;
                }

                PayUCreateOrderResponse payUResponse = gson.fromJson(responseBody, PayUCreateOrderResponse.class);
                GetPaymentUrlResponse getPaymentUrlResponse = new GetPaymentUrlResponse(payUResponse.getRedirectUri());

                return ResponseEntity.ok(getPaymentUrlResponse);
            }

        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> processPaymentNotify(PayUWebhook payUWebhook) {
        System.out.println("PayUWebhook: " + payUWebhook);

        if (!payUWebhook.getOrder().getStatus().equals("COMPLETED")) {
            ResponseEntity.ok().build();
        }

        String meetingId = payUWebhook.getOrder().getExtOrderId();
        boolean savePaymentInTutoringService = savePaymentInTutoringService(meetingId);

        if (!savePaymentInTutoringService) {
            return ResponseEntity.internalServerError().build();
        }

        AppointmentPayment appointmentPayment = new AppointmentPayment(
                LocalDateTime.now(),
                Double.parseDouble(payUWebhook.getOrder().getTotalAmount()) / 100.00,
                meetingId,
                payUWebhook.getOrder().getStatus()
        );

        appointmentPaymentRepository.saveAndFlush(appointmentPayment);

        return ResponseEntity.ok().build();
    }

    private boolean savePaymentInTutoringService(String meetingId) {
        String url = "http://" + servicesURL.getTutoringServiceUrl() + "/internal/payment/" + meetingId;

        HttpGet request = new HttpGet(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.printf("Request url: %s | Status code: %d%n", url, statusCode);

            return statusCode == 200;

        } catch (IOException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            return false;
        }
    }
}
