package com.eduvy.tutoring.service.impl;

import com.eduvy.tutoring.dto.payment.OrderRequest;
import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.service.PaymentService;
import com.eduvy.tutoring.utils.Utils;
import com.google.gson.Gson;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.Closeable;

@Service
public class PaymentServiceImpl implements PaymentService {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    Gson gson = new Gson();

    @Override
    public String getPaymentUrl(Appointment appointment) {
        OrderRequest orderRequest = new OrderRequest(
                Utils.encodeAppointmentId(appointment),
                appointment.getPrice()
        );

//        String url = "http://tutoring-service:8087/internal/get-payment-link/" + appointment;
//        String url = "http://localhost:8087/internal/get-payment-link/" + meetingId;

        return null;
    }


    @Override
    public ResponseEntity<Void> savePaymentInAppointment(String paymentId) {
        return null;
    }
}
