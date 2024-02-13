package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts(Pageable pageable);

    ProductResponse getOneProduct(Long id);

    List<ProductResponse> getProductsByCategoryId(Long id);
    List<ProductResponse> findByKeyword(String keyword);

    void saveImg(byte[] img);
}
