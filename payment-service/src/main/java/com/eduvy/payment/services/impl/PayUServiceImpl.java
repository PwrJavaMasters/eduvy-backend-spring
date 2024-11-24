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

//    @Value("${payu.client_id}")
    private String clientId = "300746";

//    @Value("${payu.client_secret}")
    private String clientSecret = "2ee86a66e5d97e3fadc400c9f19b065d";

//    @Value("${payu.oauth_endpoint}")
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
        System.out.println(payUService.getAccessToken());
    }
}
