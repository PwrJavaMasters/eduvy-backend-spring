package com.eduvy.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayUCreateOrderResponse {

    private StatusDto status;
    private String redirectUri;
    private String orderId;
    private String extOrderId;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusDto {
        private String statusCode;
    }
}
