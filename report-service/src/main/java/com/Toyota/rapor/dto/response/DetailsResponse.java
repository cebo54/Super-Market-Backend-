package com.Toyota.rapor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailsResponse {

    private int totalSales;

    private int totalSoldProducts;
    private int creditCardPayments;
    private int cashPayments;
    private double totalAmount;


    public void incrementTotalSales(){
        totalSales++;
    }

    public void incrementTotalSoldProducts(int quantity) {
        totalSoldProducts +=quantity;
    }

    public void addTotalAmount(double amount) {
        totalAmount +=amount;
    }

    public void incrementCreditCardPayments() {
        creditCardPayments++;
    }

    public void incrementCashPayments() {
        cashPayments++;
    }
}
