package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts(Integer page,Integer size,String sortBy,String filter);

    ProductResponse getOneProduct(Long id);

    List<ProductResponse> getProductsByCategoryId(Long id);


    String addImg(MultipartFile file, Long id) throws IOException;
}
