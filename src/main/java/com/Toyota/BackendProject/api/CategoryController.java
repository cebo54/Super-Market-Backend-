package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.dto.response.CategoryResponse;
import com.Toyota.BackendProject.service.Abstract.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/getAllCategories")
    public List<CategoryResponse> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @GetMapping("/getOneCategory/{id}")
    public CategoryResponse getOneCategory(@PathVariable Long id){

        return categoryService.getOneCategory(id);
    }


}
