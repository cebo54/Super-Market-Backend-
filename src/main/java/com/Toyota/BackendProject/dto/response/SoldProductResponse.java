package com.Toyota.BackendProject.dto.response;

import com.Toyota.BackendProject.Entity.SoldProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldProductResponse {
    private String productName;
    private int quantity;

    private double price;

    private String brand;

    public static SoldProductResponse convert(SoldProduct soldProduct){
        return SoldProductResponse.builder()
                .productName(soldProduct.getProduct().getName())
                .quantity(soldProduct.getQuantity())
                .price(soldProduct.getProduct().getPrice())
                .brand(soldProduct.getProduct().getBrand())
                .build();
    }
}
