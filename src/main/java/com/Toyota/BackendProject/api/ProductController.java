package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.Util.GenericResponse;
import com.Toyota.BackendProject.dto.response.ProductResponse;
import com.Toyota.BackendProject.service.Abstract.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/getAllProducts")
    public GenericResponse<List<ProductResponse>> getAllProducts(@RequestParam(defaultValue = "0",name = "page")Integer page,
                                                                 @RequestParam(defaultValue = "2",name = "size") Integer size,
                                                                 @RequestParam(defaultValue = "id",name = "sortBy")String sortBy,
                                                                 @RequestParam(defaultValue = "",name = "filter")String filter){
        final List<ProductResponse>products=productService.getAllProducts(page,size,sortBy,filter);
        return GenericResponse.successResult(products,"success.message.successful");
    }

    @GetMapping("/getOneProduct/{id}")
    public GenericResponse<ProductResponse> getOneProduct(@PathVariable Long id){

        return GenericResponse.successResult(productService.getOneProduct(id),"success.message.successful");
    }

    @GetMapping("/getProductsByCategory/{id}")
    public GenericResponse<List<ProductResponse>>getProductsByCategoryId(@PathVariable("id") Long id){
        List<ProductResponse> products=productService.getProductsByCategoryId(id);
        return GenericResponse.successResult(products,"success.message.successful");
    }

    @GetMapping("/search")
    public GenericResponse<List<ProductResponse>> searchProducts(@RequestParam("keyword") String keyword) {
        List<ProductResponse> products = productService.findByKeyword(keyword);
        return GenericResponse.successResult(products,"success.message.successful");
    }




}
