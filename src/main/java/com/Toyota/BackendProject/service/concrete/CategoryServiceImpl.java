package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.CategoryRepository;
import com.Toyota.BackendProject.Entity.Category;
import com.Toyota.BackendProject.dto.response.CategoryResponse;
import com.Toyota.BackendProject.service.Abstract.CategoryService;
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
        List<CategoryResponse> categoryViewResponses=categories.stream().map(CategoryResponse::convert).collect(Collectors.toList());
        return categoryViewResponses;
    }

    @Override
    public CategoryResponse getOneCategory(Long id) {
        Category category=categoryRepository.findById(id).orElseThrow();
        CategoryResponse cvr= CategoryResponse.convert(category);
        return cvr;
    }


}
