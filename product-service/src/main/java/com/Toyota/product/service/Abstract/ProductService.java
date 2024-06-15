package com.Toyota.product.service.Abstract;


import com.Toyota.product.dto.request.ProductRequest;
import com.Toyota.product.dto.response.ProductResponse;
import com.Toyota.product.dto.response.StockResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts(Integer isActive, Integer page, Integer size, String sortBy, String filter);

    ProductResponse getOneProduct(Long id);

    List<ProductResponse> getProductsByCategoryId(Long id);


    String addImg(MultipartFile file, Long id) throws IOException;

    ProductResponse save(ProductRequest productRequest);


    void changeStatus(Long id);

    List<StockResponse> isInStock(List<String>name);
}
