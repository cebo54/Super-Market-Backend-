package com.Toyota.sale.service.concrete;


import com.Toyota.sale.dao.CampaignRepository;
import com.Toyota.sale.dao.ProductRepository;
import com.Toyota.sale.dao.SaleRepository;
import com.Toyota.sale.dao.SoldProductRepository;
import com.Toyota.sale.dto.request.SaleRequest;
import com.Toyota.sale.dto.request.SoldProductRequest;
import com.Toyota.sale.dto.response.SaleResponse;
import com.Toyota.sale.entity.*;
import com.Toyota.sale.service.Abstract.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.log4j.Logger;
import java.util.stream.Collectors;

/**
 * Implementation of the SaleService interface.
 * This service handles the process of making a sale, including calculating total amounts,
 * applying discounts, updating product stocks, and saving sale and sold product information.
 */

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    public static final Logger logger = Logger.getLogger(SaleServiceImpl.class);
    private final SaleRepository saleRepository;
    private final SoldProductRepository soldProductRepository;
    private final ProductRepository productRepository;
    private final CampaignRepository campaignRepository;

    /**
     * Processes a sale request and returns the sale response.
     *
     * @param saleRequest The sale request data.
     * @return A SaleResponse object containing the sale details.
     */

    @Override
    public SaleResponse sale(SaleRequest saleRequest) {
        logger.info("Starting sale process for request: " + saleRequest);
        List<SoldProduct> soldProducts = saleRequest.getSoldProducts().stream()
                .map(soldProductRequest ->
                {
                    SoldProduct soldProduct =new SoldProduct();
                    Product product=productRepository.findById(soldProductRequest.getProductId())
                            .orElseThrow(()->new RuntimeException("Product Not Found"));
                    soldProduct.setProduct(product);

                    Category category=product.getCategory();
                    Campaign campaign=campaignRepository.findCampaignByCategoryId(category.getId());
                    soldProduct.setCampaign(campaign);

                    soldProduct.setQuantity(soldProductRequest.getQuantity());

                    return soldProduct;
                })
                .collect(Collectors.toList());
        double totalAmount=calculateTotalAmount(soldProducts);
        if(totalAmount > saleRequest.getReceivedMoney()){
            logger.info(String.format("your budget is not enough you need %s cash",totalAmount-saleRequest.getReceivedMoney()));
            throw new RuntimeException(String.format("your budget is not enough you need %s cash",totalAmount-saleRequest.getReceivedMoney()));
        }
        Sale sale = Sale.builder()
                .paymentDate(LocalDateTime.now())
                .cashierName(saleRequest.getCashierName())
                .paymentType(saleRequest.getPaymentType())
                .receivedMoney(saleRequest.getReceivedMoney())
                .totalAmount(totalAmount)
                .change(saleRequest.getReceivedMoney()-totalAmount)
                .soldProducts(soldProducts)
                .build();
        saleRepository.save(sale);
        logger.info("Sale saved with ID: " + sale.getId());
        for(SoldProduct soldProduct:soldProducts){
            soldProduct.setSale(sale);
            soldProductRepository.save(soldProduct);
            logger.info("SoldProduct saved with ID: " + soldProduct.getId());
        }
        updateProductStocks(saleRequest.getSoldProducts());
        logger.info("Product stocks updated for sale.");

        return SaleResponse.convert(sale,soldProducts);


    }

    /**
     * Updates the stock quantities of products based on the sold products list.
     *
     * @param soldProducts The list of sold products.
     */
    private void updateProductStocks(List<SoldProductRequest> soldProducts) {
        for (SoldProductRequest soldProductRequest : soldProducts) {
            Product product = productRepository.findById(soldProductRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int soldQuantity = soldProductRequest.getQuantity();
            int currentStock = product.getStock();
            int updatedStock = currentStock - soldQuantity;
            if(updatedStock<0){
                updatedStock=0;
            }
            // set updated stock
            product.setStock(updatedStock);
            if(updatedStock==0){
                product.setActive(false);
            }
            // save new version of product
            productRepository.save(product);
            logger.info("Product stock updated for product ID: " + product.getId() + ". New stock: " + updatedStock);
        }
    }

    /**
     * Calculates the total amount for the list of sold products, including any applicable discounts.
     *
     * @param soldProducts The list of sold products.
     * @return The total amount after discounts.
     */
    private double calculateTotalAmount(List<SoldProduct> soldProducts) {
        double totalAmount = 0;

        for (SoldProduct soldProduct:soldProducts) {
            double productPrice = soldProduct.getProduct().getPrice();
            int quantity =soldProduct.getQuantity();
            double discount = calculateDiscount(soldProduct.getCampaign(), productPrice, quantity);
            totalAmount += (productPrice * quantity) - discount;
        }
        logger.info("Total amount calculated: " + totalAmount);

        return totalAmount;
    }

    /**
     * Calculates the discount for a given campaign.
     *
     * @param campaign    The campaign associated with the product.
     * @param productPrice The price of the product.
     * @param quantity     The quantity of the product.
     * @return The calculated discount amount.
     */
    public double calculateDiscount(Campaign campaign,double productPrice,int quantity) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
        double discount = 0;
        Long campaignId=campaign.getId();
        if (campaignId != null) {
            if (campaignId==1) {
                if(quantity==3)
                    discount =productPrice;
            }
            if (campaignId==2) {
                discount = productPrice * quantity * 0.25;
            }
            if (campaignId==3) {
                if (quantity == 2) {
                    int freeProductCount = 1;
                    discount = freeProductCount * productPrice;
                }
            }
            if (campaignId==4) {
                    discount = productPrice * quantity * 0.10;
            }


        }
        logger.info("Discount calculated for campaign ID: " + campaignId + " is: " + discount);
        return discount;
    }
}
