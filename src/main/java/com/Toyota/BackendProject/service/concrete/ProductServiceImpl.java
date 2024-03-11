package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.CategoryRepository;
import com.Toyota.BackendProject.Dao.ProductRepository;
import com.Toyota.BackendProject.Entity.Product;
import com.Toyota.BackendProject.dto.request.ProductRequest;
import com.Toyota.BackendProject.dto.response.ProductResponse;
import com.Toyota.BackendProject.service.Abstract.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse save(ProductRequest productRequest) {
        Product product=Product.builder()
                .name(productRequest.getName())
                .brand(productRequest.getBrand())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .expiration_date(productRequest.getExpiration_date())
                .description(productRequest.getDescription())
                .barcode(productRequest.getBarcode())
                .category(categoryRepository.findById(productRequest.getCategoryId())
                        .orElseThrow(()->new RuntimeException("Category Not Found")))
                .isActive(productRequest.getIsActive())
                .build();
        productRepository.save(product);

        return ProductResponse.convert(product);
    }

    @Override
    public void changeStatus(Long id) {
        Optional<Product> optionalUser=productRepository.findById(id);
        if(optionalUser.isPresent()){
            Product product=optionalUser.get();
            product.setActive(!product.isActive());
            productRepository.save(product);
        }
        else{
            throw new RuntimeException("User not found with id "+id);
        }
    }


    @Override
    public List<ProductResponse> getAllProducts(Integer isActive,Integer page,Integer size,String sortBy,String filter) {
        Pageable pageable= PageRequest.of(page,size, Sort.by(sortBy));
        Boolean[] bools = {false, true};
        boolean isActiveStatus = bools[isActive];
        Page<Product>finalRequest;
        if(filter.isEmpty()){
            finalRequest=productRepository.findByIsActive(isActiveStatus,pageable);
        }
        else {
            finalRequest=productRepository.findByIsActiveWithFilter(isActiveStatus,pageable,filter);
        }

        return ProductResponse.convertUserListToUserViewResponse(finalRequest);
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
    public String addImg(MultipartFile file, Long id) throws IOException {
        Product product=productRepository.findById(id).orElseThrow();
        product.setImg(file.getBytes());
        productRepository.save(product);
        byte[] img= product.getImg();
        String base64img= Base64.getEncoder().encodeToString(img);
        return base64img;
    }




}
