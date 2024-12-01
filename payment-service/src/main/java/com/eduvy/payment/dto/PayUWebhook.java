package com.eduvy.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class PayUWebhook {

    private OrderDto order;
    private String localReceiptDateTime; // Present in the second webhook only
    private List<PropertyDto> properties;

    @Data
    @NoArgsConstructor
    @ToString
    public static class OrderDto {
        private String orderId;
        private String extOrderId;
        private String orderCreateDate;
        private String notifyUrl;
        private String customerIp;
        private String merchantPosId;
        private String description;
        private String currencyCode;
        private String totalAmount;
        private String status;
        private List<ProductDto> products;
        private BuyerDto buyer; // Present in the second webhook only
        private PayMethodDto payMethod; // Present in the second webhook only
    }

    @Data
    @NoArgsConstructor
    @ToString
    public static class ProductDto {
        private String name;
        private String unitPrice;
        private String quantity;
    }

    @Data
    @NoArgsConstructor
    @ToString
    public static class BuyerDto {
        private String customerId;
        private String email;
        private String firstName;
        private String lastName;
    }

    @Data
    @NoArgsConstructor
    @ToString
    public static class PayMethodDto {
        private String type;
    }

    @Data
    @NoArgsConstructor
    @ToString
    public static class PropertyDto {
        private String name;
        private String value;
    }
}

