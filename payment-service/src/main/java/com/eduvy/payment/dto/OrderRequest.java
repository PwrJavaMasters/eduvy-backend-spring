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
}
