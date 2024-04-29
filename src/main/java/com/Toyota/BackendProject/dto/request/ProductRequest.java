package com.Toyota.BackendProject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
        private String name;
        private double price;
        private String barcode;
        private int stock;
        private String brand;
        private String description;

        private Date expiration_date;
        private Long categoryId;
        private Boolean isActive;


}
