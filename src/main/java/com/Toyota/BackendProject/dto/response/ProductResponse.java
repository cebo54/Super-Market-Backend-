package com.Toyota.BackendProject.dto.response;

import com.Toyota.BackendProject.Entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String name;
    private Double price;
    private String barcode;

    private String brand;
    private String description;

    private Date expiration_date;

    private String category;

    public static ProductResponse convert(Product product){
      return ProductResponse.builder()
              .name(product.getName())
              .price(product.getPrice())
              .barcode(product.getBarcode())
              .brand(product.getBrand())
              .description(product.getDescription())
              .expiration_date(product.getExpiration_date())
              .category(product.getCategory().getName())
              .build();
    }

}
