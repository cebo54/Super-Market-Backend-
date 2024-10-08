package com.Toyota.product.api;


import com.Toyota.product.dto.response.CategoryResponse;
import com.Toyota.product.service.Abstract.CategoryService;
import com.Toyota.product.util.GenericResponse;
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
    public GenericResponse<List<CategoryResponse>> getAllCategories(){

        return GenericResponse.successResult(categoryService.getAllCategories(),"success.message.successful");
    }

    @GetMapping("/getOneCategory/{id}")
    public GenericResponse<CategoryResponse> getOneCategory(@PathVariable Long id){
        try {
            return GenericResponse.successResult(categoryService.getOneCategory(id), "success.message.successful");
        }catch (RuntimeException e){
            return GenericResponse.errorResult(e.getMessage());
        }
    }


}
