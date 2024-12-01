package com.eduvy.payment.services.impl;

import com.eduvy.payment.dto.OrderRequest;
import com.eduvy.payment.dto.PayUCreateOrderResponse;
import com.eduvy.payment.services.PayUService;
import com.eduvy.payment.services.PaymentService;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    CloseableHttpClient httpClient = HttpClients.createDefault();
    Gson gson = new Gson();

    @Override
    public String createOrder(OrderRequest orderRequest) {
        String accessToken = payUService.getAccessToken();
        System.out.println("Access Token: " + accessToken);

        try {
            // Prepare the HTTP POST request
            HttpPost httpPost = new HttpPost(orderCreateEndpoint);
            httpPost.setHeader("Authorization", "Bearer " + accessToken);
            httpPost.setHeader("Content-Type", "application/json");

            // Prepare payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("notifyUrl", "https://webhook.site/6651d5ea-b82f-40c4-9a26-51c78d32bebe");
            payload.put("customerIp", "127.0.0.1");
            payload.put("merchantPosId", merchantPosId);
            payload.put("description", "Zapłata za spotkanie");
            payload.put("currencyCode", "PLN");
            payload.put("totalAmount", orderRequest.getTotalAmount());
            payload.put("extOrderId", orderRequest.getExtOrderId());

            // Add product details
            List<Map<String, Object>> products = new ArrayList<>();
            Map<String, Object> product = new HashMap<>();
            product.put("name", "Korepetycje online");
            product.put("unitPrice", orderRequest.getTotalAmount());
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


                return payUResponse.getRedirectUri();
            }

        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }


    public static void main(String[] args) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setExtOrderId("7");
        orderRequest.setTotalAmount(10000);

        PayUService payUService = new PayUServiceImpl();
        PaymentService paymentService = new PaymentServiceImpl(payUService);
        String link = paymentService.createOrder(orderRequest);
        System.out.printf("Link: %s\n", link);
    }

}
