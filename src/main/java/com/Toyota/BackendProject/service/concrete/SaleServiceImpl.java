package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.CampaignRepository;
import com.Toyota.BackendProject.Dao.ProductRepository;
import com.Toyota.BackendProject.Dao.SaleRepository;
import com.Toyota.BackendProject.Dao.SoldProductRepository;
import com.Toyota.BackendProject.Entity.*;
import com.Toyota.BackendProject.dto.request.SaleRequest;
import com.Toyota.BackendProject.dto.request.SoldProductRequest;
import com.Toyota.BackendProject.dto.response.ProductResponse;
import com.Toyota.BackendProject.dto.response.SaleResponse;
import com.Toyota.BackendProject.service.Abstract.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.log4j.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private static final Logger logger = Logger.getLogger(SaleServiceImpl.class);
    private final SaleRepository saleRepository;
    private final SoldProductRepository soldProductRepository;
    private final ProductRepository productRepository;
    private final CampaignRepository campaignRepository;
    @Override
    public SaleResponse sale(SaleRequest saleRequest) {

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
        for(SoldProduct soldProduct:soldProducts){
            soldProduct.setSale(sale);
            soldProductRepository.save(soldProduct);
        }
        updateProductStocks(saleRequest.getSoldProducts());

        return SaleResponse.convert(sale,soldProducts);


    }
    private void updateProductStocks(List<SoldProductRequest> soldProducts) {
        for (SoldProductRequest soldProductRequest : soldProducts) {
            Product product = productRepository.findById(soldProductRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int soldQuantity = soldProductRequest.getQuantity();
            int currentStock = product.getStock();
            int updatedStock = currentStock - soldQuantity;

            // set updated stock
            product.setStock(updatedStock);

            // save new version of product
            productRepository.save(product);
        }
    }
    private double calculateTotalAmount(List<SoldProduct> soldProducts) {
        double totalAmount = 0;

        for (SoldProduct soldProduct:soldProducts) {
            double productPrice = soldProduct.getProduct().getPrice();
            int quantity =soldProduct.getQuantity();
            double discount = calculateDiscount(soldProduct.getCampaign(), productPrice, quantity);
            totalAmount += (productPrice * quantity) - discount;
        }
        return totalAmount;
    }
    private double calculateDiscount(Campaign campaign,double productPrice,int quantity) {
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
                int freeProductCount = 1 ;
                discount = freeProductCount * productPrice;
            }
            if (campaignId==4) {
                    discount = productPrice * quantity * 0.10;
            }


        }
        return discount;
    }
}
