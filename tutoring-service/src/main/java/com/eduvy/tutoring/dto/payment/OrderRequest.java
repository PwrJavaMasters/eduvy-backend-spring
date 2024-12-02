package com.eduvy.tutoring.dto.payment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private String extOrderId; // Unikalny identyfikator zamówienia w Twoim systemie
    private Double totalAmount; // Kwota w groszach (np. 100 PLN = 10000)
}
