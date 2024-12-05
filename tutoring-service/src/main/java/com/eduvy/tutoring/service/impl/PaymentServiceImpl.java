package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.dto.payment.GetPaymentUrlResponse;
import com.eduvy.tutoring.dto.payment.OrderRequest;
import com.eduvy.tutoring.dto.user.UserDetails;
import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.service.PaymentService;
import com.eduvy.tutoring.utils.ServicesURL;
import com.eduvy.tutoring.utils.Utils;
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

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    ServicesURL servicesURL;

    @Autowired
    private AppointmentRepository appointmentRepository;

    CloseableHttpClient httpClient = HttpClients.createDefault();
    Gson gson = new Gson();


    @Override
    public String getPaymentUrl(Appointment appointment) {
        OrderRequest orderRequest = new OrderRequest(
                Utils.encodeAppointmentId(appointment),
                appointment.getPrice()
        );
        String jsonPayload = gson.toJson(orderRequest);

        String url = "http://" + servicesURL.getPaymentServiceUrl() + "/internal/get-payment-link/" + appointment;

        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.printf("Request url: %s | Status code: %d%n", url, statusCode);

            String json = EntityUtils.toString(response.getEntity());
            GetPaymentUrlResponse responseObject = gson.fromJson(json, GetPaymentUrlResponse.class);
            return responseObject.getUrl();

        } catch (IOException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            return null;
        }
    }


    @Override
    @Transactional
    public ResponseEntity<Void> savePayment(String appointmentId) {
        Appointment appointment = appointmentRepository.findAppointmentById(Utils.decodeAppointmentId(appointmentId));
        if (appointment == null) {
            System.out.println("Appointment not found for payment save, id: " + appointmentId);
            return ResponseEntity.notFound().build();
        }

        appointment.setIsPaid(true);
        appointmentRepository.save(appointment);

        return ResponseEntity.ok().build();
    }
}
