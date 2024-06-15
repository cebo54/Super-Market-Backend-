package com.Toyota.product.dto.response;


import com.Toyota.product.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private String name;

    private String description;



   public static CategoryResponse convert(Category category){
       return CategoryResponse.builder()
               .name(category.getName())
               .description(category.getDescription())
               .build();
   }
}
