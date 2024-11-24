package com.eduvy.payment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private String extOrderId; // Unikalny identyfikator zamówienia w Twoim systemie
    private int totalAmount; // Kwota w groszach (np. 100 PLN = 10000)
    private String currencyCode; // Kod waluty, np. "PLN"
    private String description; // Opis zamówienia
    private String customerIp; // IP klienta (np. "127.0.0.1")
    private String notifyUrl; // URL do powiadomień o statusie płatności
    private String continueUrl; // URL, na który użytkownik zostanie przekierowany po płatności
    private List<Product> products; // Lista produktów w zamówieniu

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Product {
        private String name; // Nazwa produktu
        private int unitPrice; // Cena jednostkowa w groszach
        private int quantity; // Ilość produktu
    }
}
