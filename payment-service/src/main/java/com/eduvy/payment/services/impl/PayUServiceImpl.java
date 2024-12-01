package com.eduvy.payment.services.impl;


import com.eduvy.payment.services.PayUService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class PayUServiceImpl implements PayUService {

    private String clientId = "486591";

    private String clientSecret = "05f6c7967bfe5eeec5ac68d226418321";

    private String oauthEndpoint = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedCredentials);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(oauthEndpoint, request, Map.class);

        return response.getBody().get("access_token").toString();
    }

    public static void main(String[] args) {
        PayUService payUService = new PayUServiceImpl();
        System.out.println("Access token for PayU: " + payUService.getAccessToken());
    }
}
