package com.Toyota.BackendProject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldProductRequest {
    private Long productId;
    private int quantity;
    private String brand;

    private double price;
}
