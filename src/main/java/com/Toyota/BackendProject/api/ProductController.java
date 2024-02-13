package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.dto.response.ProductResponse;
import com.Toyota.BackendProject.service.Abstract.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductResponse>> getAllProducts(Pageable pageable){
        final List<ProductResponse>products=productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/getOneProduct/{id}")
    public ProductResponse getOneProduct(@PathVariable Long id){

        return productService.getOneProduct(id);
    }

    @GetMapping("/getProductsByCategory/{id}")
    public List<ProductResponse>getProductsByCategoryId(@PathVariable("id") Long id){
        List<ProductResponse> products=productService.getProductsByCategoryId(id);
        return products;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam("keyword") String keyword) {
        List<ProductResponse> products = productService.findByKeyword(keyword);
        return ResponseEntity.ok(products);
    }




}
