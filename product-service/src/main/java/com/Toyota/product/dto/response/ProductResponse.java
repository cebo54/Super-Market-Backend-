package com.Toyota.product.dto.response;


import com.Toyota.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date expiration_date;

    private String category;

    private byte[] img;
    private Boolean isActive;

    public static ProductResponse convert(Product product){
      return ProductResponse.builder()
              .name(product.getName())
              .price(product.getPrice())
              .barcode(product.getBarcode())
              .brand(product.getBrand())
              .description(product.getDescription())
              .expiration_date(product.getExpiration_date())
              .category(product.getCategory().getName())
              .img(product.getImg())
              .isActive(product.isActive())
              .build();
    }
    public static List<ProductResponse> convertUserListToUserViewResponse(Page<Product> products){
        return products.stream().map(ProductResponse::convert).collect(Collectors.toList());
    }

}
