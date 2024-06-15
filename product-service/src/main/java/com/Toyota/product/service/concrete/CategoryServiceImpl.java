package com.Toyota.product.service.concrete;


import com.Toyota.product.dao.CategoryRepository;
import com.Toyota.product.dto.response.CategoryResponse;
import com.Toyota.product.entity.Category;
import com.Toyota.product.service.Abstract.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;


    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryViewResponses = categories.stream().map(CategoryResponse::convert).collect(Collectors.toList());
        return categoryViewResponses;
    }

    @Override
    public CategoryResponse getOneCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        CategoryResponse cvr = CategoryResponse.convert(category);
        return cvr;
    }


}
