package com.Toyota.rapor.dto.response;


import com.Toyota.rapor.entity.SoldProduct;
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

    public static SoldProductResponse convert(SoldProduct soldProduct){
        return SoldProductResponse.builder()
                .productName(soldProduct.getProduct().getName())
                .quantity(soldProduct.getQuantity())
                .price(soldProduct.getProduct().getPrice())
                .build();
    }

}
