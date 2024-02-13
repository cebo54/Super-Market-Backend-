package com.Toyota.BackendProject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleRequest {

    private List<SoldProductRequest> soldProducts;

    private double receivedMoney;

    private String paymentType;

    private String cashierName;




}
