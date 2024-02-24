package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.ProductRepository;
import com.Toyota.BackendProject.Entity.Product;
import com.Toyota.BackendProject.dto.response.ProductResponse;
import com.Toyota.BackendProject.service.Abstract.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    public List<ProductResponse> getAllProducts(Integer page,Integer size,String sortBy,String filter) {
        Pageable pageable= PageRequest.of(page,size, Sort.by(sortBy));
        return productRepository.findAll(pageable,filter).stream().map(ProductResponse::convert).collect(Collectors.toList());
    }
    @Override
    public ProductResponse getOneProduct(Long id) {
        Product product=productRepository.findById(id).orElseThrow();
        return ProductResponse.convert(product);
    }

    @Override
    public List<ProductResponse> getProductsByCategoryId(Long id) {
        List<Product>products=productRepository.findAllByCategoryId(id);
        List<ProductResponse>pvr=products.stream().map(ProductResponse::convert).collect(Collectors.toList());
        return pvr;
    }
    @Override
    public List<ProductResponse> findByKeyword(String keyword) {
        List<Product> productList = productRepository.findByKeyword(keyword);
        return productList.stream()
                .map(ProductResponse::convert)
                .collect(Collectors.toList());
    }


    @Override
    public void saveImg(byte[] img) {
        Product product=new Product();
        product.setImgProp(img);
        productRepository.save(product);
    }


}
