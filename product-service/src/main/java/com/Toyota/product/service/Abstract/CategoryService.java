package com.Toyota.product.service.Abstract;

import com.Toyota.product.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getOneCategory(Long id);


}
