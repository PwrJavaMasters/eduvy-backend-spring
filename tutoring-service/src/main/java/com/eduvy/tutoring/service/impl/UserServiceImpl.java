package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.UserInfoDetails;
import com.eduvy.tutoring.dto.user.UserDetails;
import com.eduvy.tutoring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class UserServiceImpl implements UserService {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) ->
                    LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .create();

    @Override
    public String getUserMail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();
        String email = userDetails.getEmail();

        return email;
    }

    @Override
    public UserDetails getUserDetails() {
        String userMail = getUserMail();
        String url = "http://localhost:8083/internal/user-details/" + userMail;

        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.printf("Request url: " + url + " | status code: " + statusCode );

            if (statusCode != 200) {
                return null;
            }

            String json = EntityUtils.toString(response.getEntity());
            return gson.fromJson(json, UserDetails.class);

        } catch (IOException e) {
            return null;
        }
    }
}
