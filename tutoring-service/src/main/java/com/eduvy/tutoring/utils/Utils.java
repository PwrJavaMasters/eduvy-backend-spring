package com.eduvy.tutoring.utils;

import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.model.TutorProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Random;

@Component
public class Utils {

    private static Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        Utils.environment = environment;
    }

    private static Hashids hashids = new Hashids("eduvy", 7);// do not change salt

    public static String encodeTutorProfileId(TutorProfile tutorProfile) {
        return "EDUVY-TUTOR_" + hashids.encode(tutorProfile.getId());
    }

    public static Long decodeTutorProfileId(String hashId) {
        return decodeId(hashId, "EDUVY-TUTOR");
    }

    public static String encodeAppointmentId(Appointment appointment) {
        return "EDUVY-APPOINTMENT_" + hashids.encode(appointment.getId());
    }

    public static Long decodeAppointmentId(String hashId) {
        return decodeId(hashId, "EDUVY-APPOINTMENT");
    }

    public static Long decodeId(String hashId, String idType) {
        try {
            if (hashId.startsWith(idType)) {
                String[] splited = hashId.split("_");
                hashId = splited[splited.length - 1];
            }

            return hashids.decode(hashId)[0];

        } catch (Exception e) {
            return null;
        }
    }

    public static String randomString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();

        return saltStr;
    }

    public static <T, R> R sendPostRequest(T requestObject, String requestUrl, TypeReference<R> typeReference, String authorizationHeader) {

        R responseObject;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(requestObject);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authorizationHeader)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(6))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            responseObject = objectMapper.readValue(response.body(), typeReference);

        } catch (Exception exception) {
            System.err.print("Exception occurred while sending request to: " + requestUrl + ". Message: " + exception);
            return null;
        }

        return responseObject;
    }

    public static <R> R sendGetRequest(String requestUrl, TypeReference<R> typeReference, String authorizationHeader) {

        R responseObject;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authorizationHeader)
                    .GET()
                    .timeout(Duration.ofSeconds(6))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            responseObject = objectMapper.readValue(response.body(), typeReference);

        } catch (Exception exception) {
            System.err.print("Exception occurred while sending request to: " + requestUrl + ". Message: " + exception.getMessage());
            return null;
        }

        return responseObject;
    }

    public static <T> HttpResponse<String> sendPostRequest(String requestUrl, String authorizationHeader) {

        HttpResponse<String> response;
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authorizationHeader)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofSeconds(6))
                    .build();

            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception exception) {
            System.err.print("Exception occurred while sending request to: \" + requestUrl + \". Message: \" + exception.getMessage()");
            return null;
        }

        return response;
    }

    public static  <T> String convertToBase64(T object) {
        String jsonString;

        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(object);

        } catch (Exception e) {
            System.err.print("Error occurred while mapping object to base64.");
            return null;
        }


        return Base64.getEncoder().encodeToString(jsonString.getBytes());
    }

    public static  <T> T convertFromBase64(String base64String, Class<T> tClass) {
        try {
            String jsonString = new String(Base64.getDecoder().decode(base64String));

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, tClass);

        } catch (Exception e) {
            System.err.print("Error occurred while converting base64 to object.");
            return null;
        }
    }

    public static boolean isStringValid(String string) {
        if (string == null || string.isBlank() || string.length() > 256) {
            return false;
        }
        String regex = "^[\\p{L}\\p{Nd} ,.!?;:()*&^%$#@+_\\[\\]\"/\\-]*$";

        return string.matches(regex);
    }

    public static boolean isStringValidBlankAccepted(String string) {
        if (string == null || string.length() > 256) {
            return false;
        }
        String regex = "^[\\p{L}\\p{Nd} ,.!?;:()*&^%$#@+_\\[\\]\"/\\-]*$";
        return string.matches(regex);
    }
}