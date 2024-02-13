package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getOneCategory(Long id);


}
