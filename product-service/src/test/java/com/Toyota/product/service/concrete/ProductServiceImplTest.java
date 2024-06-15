package com.Toyota.product.service.concrete;

import com.Toyota.product.dao.CategoryRepository;
import com.Toyota.product.dao.ProductRepository;
import com.Toyota.product.dto.request.ProductRequest;
import com.Toyota.product.dto.response.ProductResponse;
import com.Toyota.product.dto.response.StockResponse;
import com.Toyota.product.entity.Category;
import com.Toyota.product.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductServiceImpl productService;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp(){
        productRepository= Mockito.mock(ProductRepository.class);
        categoryRepository=Mockito.mock(CategoryRepository.class);
        productService=new ProductServiceImpl(productRepository,categoryRepository);
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
                .isActive(true)
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
    void shouldSaveProductToDb_whenCategoryExist(){
        ProductRequest productRequest=new ProductRequest();
        Category category=generateCategory();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        Product product=generateProduct();
        productRequest.setName(product.getName());
        productRequest.setBrand(product.getBrand());
        productRequest.setPrice(product.getPrice());
        productRequest.setStock(product.getStock());
        productRequest.setExpiration_date(product.getExpiration_date());
        productRequest.setDescription(product.getDescription());
        productRequest.setBarcode(product.getBarcode());
        productRequest.setIsActive(product.isActive());
        productRequest.setCategoryId(product.getCategory().getId());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response=productService.save(productRequest);

        verify(productRepository, times(1)).save(any());

        assertEquals(productRequest.getName(),response.getName());
        assertEquals(productRequest.getDescription(),response.getDescription());

    }
    @Test
    void shouldChangeStatusOfProducts_TrueToFalse_FalseToTrue(){
        Product product=generateProduct();
        product.setId(1L);
        product.setActive(true);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.changeStatus(product.getId());

        verify(productRepository,times(1)).save(product);

        assertFalse(product.isActive());
    }
    @Test
    public void testChangeStatus_ProductNotFound() {
        Long id=2L;
        Product product=generateProduct();
        product.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.changeStatus(id));

        assertEquals("User not found with id "+id,exception.getMessage());
    }
    @Test
    void shouldReturnAllProducts_withNoFilter(){
        Page<Product>products=mock(Page.class);
        when(productRepository.findByIsActive(true,PageRequest.of(0,10,Sort.by("name")))).thenReturn(products);
        when(products.getContent()).thenReturn(new ArrayList<>());

        List<ProductResponse> result = productService.getAllProducts(1, 0, 10, "name", "");

        verify(productRepository, times(1)).findByIsActive(true, PageRequest.of(0, 10, Sort.by("name")));

        // Verify that an empty list is returned
        assertEquals(0, result.size());
    }
    @Test
    void shouldReturnAllProducts_withFilter(){
        Page<Product>products=mock(Page.class);
        when(productRepository.findByIsActiveWithFilter(true,PageRequest.of(0,10,Sort.by("name")),"filter")).thenReturn(products);
        when(products.getContent()).thenReturn(new ArrayList<>());

        List<ProductResponse> result = productService.getAllProducts(1, 0, 10, "name", "filter");

        verify(productRepository, times(1)).findByIsActiveWithFilter(true, PageRequest.of(0, 10, Sort.by("name")),"filter");

        // Verify that an empty list is returned
        assertEquals(0, result.size());
    }
    @Test
    void shouldReturnTrue_ifProductIsInStock_shouldReturnFalse_ifProductIsNotInStock(){
        Product product1=generateProduct();
        product1.setName("product1");
        product1.setActive(true);
        Product product2=generateProduct();
        product2.setName("product2");
        product2.setActive(false);
        List<String>names=Arrays.asList(product1.getName(),product2.getName());
        List<Product>products=Arrays.asList(product1,product2);

        when(productRepository.findByNameIn(names)).thenReturn(products);
        List<StockResponse>stockResponses=productService.isInStock(names);

        assertEquals(2, stockResponses.size()); // Check if all products are returned
        assertEquals("product1", stockResponses.get(0).getName()); // Check if name matches
        assertEquals(true, stockResponses.get(0).isActive()); // Check if isActive matches
        assertEquals("product2", stockResponses.get(1).getName());
        assertEquals(false, stockResponses.get(1).isActive());
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

        verify(productRepository,times(1)).findById(id);
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

        verify(productRepository,times(1)).findAllByCategoryId(id);
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
        verify(productRepository,times(1)).save(product);
        assertNotNull(base64);
        assertEquals(Base64.getEncoder().encodeToString(imgData),base64);


    }

    @AfterEach
    public void tearDown(){

    }
}