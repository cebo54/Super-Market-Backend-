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
public class SoldProductResponse2 {

    private String productName;
    private int quantity;

    private double price;

    public static SoldProductResponse2 convert(SoldProduct soldProduct){
        return SoldProductResponse2.builder()
                .productName(soldProduct.getProduct().getName())
                .quantity(soldProduct.getQuantity())
                .price(soldProduct.getProduct().getPrice())
                .build();
    }

}
