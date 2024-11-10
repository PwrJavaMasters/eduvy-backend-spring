package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.dto.user.UserDetails;
import com.eduvy.tutoring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final Gson gson = new Gson();

    @Override
    public String getUserMail() {
        return "";
    }

    @Override
    public UserDetails getUserDetails() {
        String userMail = getUserMail();
        String url = "http://localhost:8083/user-details/" + userMail;

        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            String json = EntityUtils.toString(response.getEntity());
            return gson.fromJson(json, UserDetails.class);

        } catch (IOException e) {
            return null;
        }
    }
}
