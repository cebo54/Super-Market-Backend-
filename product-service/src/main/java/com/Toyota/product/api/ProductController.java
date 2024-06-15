package com.Toyota.product.api;


import com.Toyota.product.dto.request.ProductRequest;
import com.Toyota.product.dto.response.ProductResponse;
import com.Toyota.product.dto.response.StockResponse;
import com.Toyota.product.service.Abstract.ProductService;
import com.Toyota.product.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for managing products via HTTP requests.
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = Logger.getLogger(ProductController.class);
    private final ProductService productService;


    /**
     * Endpoint to add a new product.
     *
     * @param productRequest The request body containing details of the product to be added.
     * @return A GenericResponse containing the added ProductResponse or an error message.
     */
    @PostMapping("/addProduct")
    public GenericResponse<ProductResponse> addProduct(@RequestBody ProductRequest productRequest){
        logger.info("Received request to add product: " + productRequest.getName());
        try {
            ProductResponse productResponse = productService.save(productRequest);
            logger.info("Product added successfully: " + productResponse.getName());
            return GenericResponse.successResult(productResponse, "success.message.dataSaved");
        } catch (Exception e) {
            logger.error("Error while adding product: " + productRequest.getName(), e);
            return GenericResponse.errorResult("success.message.error");
        }
    }

    /**
     * Endpoint to change the status (active/inactive) of a product.
     *
     * @param id The ID of the product to change the status.
     * @return A GenericResponse indicating success or failure.
     */
    @PostMapping("/changeStatus/{id}")
    public GenericResponse<?>changeStatus(@PathVariable Long id){
        logger.info("Received request to change status for product with ID: " + id);
        try {
            productService.changeStatus(id);
            logger.info("Status changed successfully for product with ID: " + id);
            return GenericResponse.success("success.message.status");
        } catch (RuntimeException e) {
            logger.error("Error while changing status for product with ID: " + id, e);
            return GenericResponse.errorResult("success.message.error");
        }
    }

    /**
     * Endpoint to fetch all products with optional filters, pagination, and sorting.
     *
     * @param isActive Specifies whether to fetch active products (1 for active, 0 for inactive).
     * @param page The page number to fetch.
     * @param size The size of the page to fetch.
     * @param sortBy The field to sort by.
     * @param filter Additional filter criteria.
     * @return A GenericResponse containing a list of ProductResponse objects.
     */
    @GetMapping("/getAllProducts")
    public GenericResponse<List<ProductResponse>> getAllProducts(@RequestParam(defaultValue = "1",required = false)Integer isActive,
                                                                 @RequestParam(defaultValue = "0",name = "page")Integer page,
                                                                 @RequestParam(defaultValue = "2",name = "size") Integer size,
                                                                 @RequestParam(defaultValue = "id",name = "sortBy")String sortBy,
                                                                 @RequestParam(defaultValue = "",name = "filter")String filter){
        logger.info("Received request to listing all products");
        try {
            final List<ProductResponse> products = productService.getAllProducts(isActive, page, size, sortBy, filter);
            logger.info("Products listed successfully");
            return GenericResponse.successResult(products, "success.message.successful");
        }catch (RuntimeException e) {
            logger.error("Produts are not listed");
            return GenericResponse.errorResult("success.message.error");
        }
    }

    /**
     * Endpoint to fetch details of a single product by its ID.
     *
     * @param id The ID of the product to fetch.
     * @return A GenericResponse containing the details of the fetched product.
     */
    @GetMapping("/getOneProduct/{id}")
    public GenericResponse<ProductResponse> getOneProduct(@PathVariable Long id){

        return GenericResponse.successResult(productService.getOneProduct(id),"success.message.successful");
    }

    /**
     * Endpoint to fetch all products belonging to a specific category.
     *
     * @param id The ID of the category to fetch products for.
     * @return A GenericResponse containing a list of ProductResponse objects.
     */
    @GetMapping("/getProductsByCategory/{id}")
    public GenericResponse<List<ProductResponse>>getProductsByCategoryId(@PathVariable("id") Long id){
        List<ProductResponse> products=productService.getProductsByCategoryId(id);
        return GenericResponse.successResult(products,"success.message.successful");
    }

    /**
     * Endpoint to add an image to a product by its ID.
     *
     * @param file The image file to be added.
     * @param id The ID of the product to add the image to.
     * @return A Base64 encoded string representing the added image.
     * @throws IOException If there is an error reading the image file.
     */
    @PostMapping("/addImg/{id}")
    public String addImg(@RequestParam("file")MultipartFile file ,@PathVariable("id")Long id) throws IOException {
        return productService.addImg(file,id);
    }
    /**
     * Endpoint to check the stock availability for a list of product names.
     *
     * @param name The list of product names to check.
     * @return A list of StockResponse objects indicating the stock status of each product.
     */
    @GetMapping("/isInStock")
    public List<StockResponse>isInStock(@RequestParam List<String>name){

        return productService.isInStock(name);
    }






}
