package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.ProductRepository;
import com.Toyota.BackendProject.Entity.Category;
import com.Toyota.BackendProject.Entity.Product;
import com.Toyota.BackendProject.dto.response.ProductResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class ProductServiceImplTest {

    private ProductServiceImpl productService;
    private ProductRepository productRepository;
    @BeforeEach
    void setUp(){
        productRepository= Mockito.mock(ProductRepository.class);
        productService=new ProductServiceImpl(productRepository);
    }
    public Product generateProduct(){
        return Product.builder()
                .name("name")
                .price(15)
                .barcode(null)
                .brand("brand")
                .description("desc")
                .expiration_date(null)
                .category(generateCategory())
                .img(null)
                .build();
    }
    public Category generateCategory(){
        return Category.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .build();
    }

    @Test
    void shouldReturnProductResponseWithProductList() {
        int page = 0;
        int size = 10;
        String sortBy = "productName";
        String filter = "filter";
        Product product1 = generateProduct();
        Product product2 = generateProduct();
        List<Product> productList = Arrays.asList(product1, product2);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        PageImpl<Product> pageImpl = new PageImpl<>(productList, pageable, productList.size());

        // Mock ProductRepository response
        when(productRepository.findAllWithFilter(any(Pageable.class), eq(filter))).thenReturn(pageImpl);

        // Call the method under test
        List<ProductResponse> productResponses = productService.getAllProducts(page, size, sortBy, filter);

        // Verify the interactions and assertions
        Mockito.verify(productRepository, times(1)).findAllWithFilter(any(Pageable.class), eq(filter));
        assertEquals(productList.size(), productResponses.size());

    }


    @Test
    void shouldReturnOneProductWithProductResponse_whenProductIdExist() {
        Long id=1L;
        Category category=generateCategory();
        Product product=generateProduct();
        product.setId(id);
        product.setName("name");
        product.setPrice(15);
        product.setBarcode(null);
        product.setBrand("brand");
        product.setDescription("desc");
        product.setExpiration_date(null);
        product.setCategory(category);
        product.setImg(null);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductResponse productResponse=productService.getOneProduct(id);

        assertNotNull(productResponse);

        assertEquals(product.getName(),productResponse.getName());
        assertEquals(product.getPrice(),productResponse.getPrice());
        assertEquals(product.getCategory().getName(),productResponse.getCategory());

        Mockito.verify(productRepository,times(1)).findById(id);
        //Mockito.verify(productService,times(1)).getOneProduct(id);


    }

    @Test
    void shouldReturnAllProductByCategoryIdWithProductResponse_whenCategoryIdExist() {
        Long id=1L;
        Product product1=generateProduct();
        Product product2=generateProduct();
        product1.setCategory(generateCategory());
        product2.setCategory(generateCategory());
        List<Product>productList=Arrays.asList(product1,product2);

        Mockito.when(productRepository.findAllByCategoryId(id)).thenReturn(productList);
        List<ProductResponse>productResponses=productService.getProductsByCategoryId(id);
        assertNotNull(productResponses);

        Mockito.verify(productRepository,times(1)).findAllByCategoryId(id);
        assertEquals(productList.size(), productResponses.size());
        for (int i = 0; i < productList.size(); i++) {
            assertEquals(productList.get(i).getName(), productResponses.get(i).getName());
            assertEquals(productList.get(i).getPrice(), productResponses.get(i).getPrice());
            assertEquals(productList.get(i).getCategory().getName(), productResponses.get(i).getCategory());
        }
    }

    @Test
    void shouldAddImgToProductByIdAndReturnBase64Img_whenProductIdAndFileExist() throws IOException {
        Long id=1l;
        byte[] imgData="img Data".getBytes();
        MockMultipartFile file=new MockMultipartFile("file","test.jpg","img/jpeg",imgData);
        Product product=generateProduct();
        product.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        String base64=productService.addImg(file,id);
        Mockito.verify(productRepository,times(1)).save(product);
        assertNotNull(base64);
        assertEquals(Base64.getEncoder().encodeToString(imgData),base64);


    }

    @AfterEach
    public void tearDown(){

    }
}