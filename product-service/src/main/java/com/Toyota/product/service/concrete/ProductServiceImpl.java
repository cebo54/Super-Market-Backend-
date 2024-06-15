package com.Toyota.product.service.concrete;

import com.Toyota.product.dao.CategoryRepository;
import com.Toyota.product.dao.ProductRepository;
import com.Toyota.product.dto.request.ProductRequest;
import com.Toyota.product.dto.response.ProductResponse;
import com.Toyota.product.dto.response.StockResponse;
import com.Toyota.product.entity.Product;
import com.Toyota.product.service.Abstract.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
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


/**
 * Service implementation for managing products.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = Logger.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    /**
     * Saves a new product based on the provided product request.
     *
     * @param productRequest The details of the product to be saved.
     * @return A ProductResponse containing the details of the saved product.
     */
    @Override
    public ProductResponse save(ProductRequest productRequest) {
        logger.info("Saving product: " + productRequest.getName());
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
        logger.info("Product saved successfully: " + product.getName());
        return ProductResponse.convert(product);
    }

    /**
     * Changes the active status of a product by its ID.
     *
     * @param id The ID of the product to change the status.
     */
    @Override
    public void changeStatus(Long id) {
        logger.info("Changing status for product with ID: " + id);
        Optional<Product> optionalProduct=productRepository.findById(id);
        if(optionalProduct.isPresent()){
            Product product=optionalProduct.get();
            product.setActive(!product.isActive());
            productRepository.save(product);
            logger.info("Status changed successfully for product with ID: " + id);
        }
        else{
            logger.error("Product not found with ID: " + id);
            throw new RuntimeException("Product not found with id "+id);
        }
    }

    /**
     * Checks the stock availability for a list of product names.
     *
     * @param name The list of product names to check.
     * @return A list of StockResponse objects indicating the stock status of each product.
     */
    @Override
    public List<StockResponse> isInStock(List<String> name) {
        logger.info("Checking stock for products: " + name);
        return productRepository.findByNameIn(name).stream()
                .map(product ->
                    StockResponse.builder()
                            .name(product.getName())
                            .isActive(product.isActive())
                            .build()
                ).toList();

    }

    /**
     * Fetches all products with optional filters, pagination, and sorting.
     *
     * @param isActive Specifies whether to fetch active products (1 for active, 0 for inactive).
     * @param page The page number to fetch.
     * @param size The size of the page to fetch.
     * @param sortBy The field to sort by.
     * @param filter Additional filter criteria.
     * @return A list of ProductResponse objects.
     */
    @Override
    public List<ProductResponse> getAllProducts(Integer isActive,Integer page,Integer size,String sortBy,String filter) {
        logger.info("Fetching all products");
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

    /**
     * Fetches details of a single product by its ID.
     *
     * @param id The ID of the product to fetch.
     * @return A ProductResponse containing the details of the fetched product.
     */
    @Override
    public ProductResponse getOneProduct(Long id) {
        logger.info("Fetching product with ID: " + id);
        Product product=productRepository.findById(id).orElseThrow();
        return ProductResponse.convert(product);
    }

    /**
     * Fetches all products belonging to a specific category.
     *
     * @param id The ID of the category to fetch products for.
     * @return A list of ProductResponse objects belonging to the specified category.
     */
    @Override
    public List<ProductResponse> getProductsByCategoryId(Long id) {
        logger.info("Fetching products by category with ID: " + id);
        List<Product>products=productRepository.findAllByCategoryId(id);
        List<ProductResponse>pvr=products.stream().map(ProductResponse::convert).collect(Collectors.toList());
        return pvr;
    }

    /**
     * Adds an image to a product by its ID.
     *
     * @param file The image file to be added.
     * @param id The ID of the product to add the image to.
     * @return A Base64 encoded string representing the added image.
     * @throws IOException If there is an error reading the image file.
     */
    @Override
    public String addImg(MultipartFile file, Long id) throws IOException {
        logger.info("Adding image to product with ID: " + id);
        Product product=productRepository.findById(id).orElseThrow();
        product.setImg(file.getBytes());
        productRepository.save(product);
        byte[] img= product.getImg();
        String base64img= Base64.getEncoder().encodeToString(img);
        logger.info("Image added successfully to product with ID: " + id);
        return base64img;
    }




}
