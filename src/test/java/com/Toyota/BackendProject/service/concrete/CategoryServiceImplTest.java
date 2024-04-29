package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.CategoryRepository;
import com.Toyota.BackendProject.Entity.Category;
import com.Toyota.BackendProject.dto.response.CategoryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class CategoryServiceImplTest {
    private CategoryServiceImpl categoryService;
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository= Mockito.mock(CategoryRepository.class);
        categoryService=new CategoryServiceImpl(categoryRepository);
    }
    @AfterEach
    void tearDown() {
    }
    public Category generateCategory(){
        return Category.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .build();
    }

    @Test
    void shouldReturnCategoryResponseWithCategoryList() {
        Category category1=generateCategory();
        Category category2=generateCategory();
        List<Category> categoryList= Arrays.asList(category1,category2);

        Mockito.when(categoryRepository.findAll()).thenReturn(categoryList);

        List<CategoryResponse>categoryResponses=categoryService.getAllCategories();
        Mockito.verify(categoryRepository, times(1)).findAll();
        assertEquals(categoryList.size(), categoryResponses.size());
        for (int i=0 ; i<categoryList.size();i++){
            assertEquals(categoryList.get(i).getName(),categoryResponses.get(i).getName());
            assertEquals(categoryList.get(i).getDescription(),categoryResponses.get(i).getDescription());
        }
    }

    @Test
    void shouldReturnOneCategoryWithCategoryResponse_whenCategoryIdExist() {
        Long id=1L;
        Category category=generateCategory();
        category.setId(id);
        category.setName("category");
        category.setDescription("desc");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        CategoryResponse categoryResponse=categoryService.getOneCategory(id);
        assertNotNull(categoryResponse);

        assertEquals(category.getName(),categoryResponse.getName());
        assertEquals(category.getDescription(),categoryResponse.getDescription());

        Mockito.verify(categoryRepository,times(1)).findById(id);

    }
}