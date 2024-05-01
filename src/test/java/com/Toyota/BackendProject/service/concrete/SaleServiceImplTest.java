package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.CampaignRepository;
import com.Toyota.BackendProject.Dao.ProductRepository;
import com.Toyota.BackendProject.Dao.SaleRepository;
import com.Toyota.BackendProject.Dao.SoldProductRepository;
import com.Toyota.BackendProject.Entity.Campaign;
import com.Toyota.BackendProject.Entity.Category;
import com.Toyota.BackendProject.Entity.Product;
import com.Toyota.BackendProject.Entity.SoldProduct;
import com.Toyota.BackendProject.dto.request.SaleRequest;
import com.Toyota.BackendProject.dto.request.SoldProductRequest;
import com.Toyota.BackendProject.dto.response.SaleResponse;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.Toyota.BackendProject.service.concrete.SaleServiceImpl.logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

class SaleServiceImplTest {
    private SaleRepository saleRepository;
    private SoldProductRepository soldProductRepository;
    private ProductRepository productRepository;
    private CampaignRepository campaignRepository;

    private SaleServiceImpl saleService;

    @BeforeEach
    void setUp() {
        saleRepository= Mockito.mock(SaleRepository.class);
        soldProductRepository=Mockito.mock(SoldProductRepository.class);
        productRepository=Mockito.mock(ProductRepository.class);
        campaignRepository=Mockito.mock(CampaignRepository.class);
        saleService=new SaleServiceImpl(saleRepository,soldProductRepository,productRepository,campaignRepository);
    }

    @AfterEach
    void tearDown() {
    }
    public Product generateProduct(){
        return Product.builder()
                .id(1L)
                .name("name")
                .price(50)
                .brand("brand")
                .expiration_date(null)
                .category(generateCategory())
                .img(null)
                .barcode("barcode")
                .stock(100)
                .description("desc")
                .isActive(true)
                .build();

    }
    public SoldProduct generateSoldProduct(){
        return SoldProduct.builder()
                .product(generateProduct())
                .quantity(5)
                .campaign(generateCampaign())
                .build();
    }
    public Category generateCategory(){
        return Category.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .build();
    }

    public Campaign generateCampaign(){
        return Campaign.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .category(generateCategory())
                .build();
    }

    @Test
    void shouldMakeSaleAndSaveToTheRepository_IfTheBalanceIsSufficient() {
        Product product=generateProduct();
        product.setStock(5);
        Campaign campaign=generateCampaign();
        SoldProduct soldProduct=generateSoldProduct();

        SaleRequest saleRequest = new SaleRequest();

        List<SoldProductRequest> soldProductRequests = new ArrayList<>();

        SoldProductRequest soldProductRequest = new SoldProductRequest();
        soldProductRequest.setProductId(product.getId());
        soldProductRequest.setQuantity(soldProduct.getQuantity());

        soldProductRequests.add(soldProductRequest);

        saleRequest.setSoldProducts(soldProductRequests);
        saleRequest.setCashierName("Cebrail");
        saleRequest.setPaymentType("Cash");
        saleRequest.setReceivedMoney(300.0);


        Mockito.when(productRepository.findById(product.getId())).thenReturn(java.util.Optional.of(product));
        Mockito.when(campaignRepository.findCampaignByCategoryId(campaign.getId())).thenReturn(campaign);
        Mockito.when(soldProductRepository.findById(soldProduct.getId())).thenReturn(Optional.of(soldProduct));

        // Call the method under test
        SaleResponse saleResponse = saleService.sale(saleRequest);

        assertEquals("Cebrail",saleResponse.getCashierName());

        //Testing CalculateTotalAmount method
        assertEquals(product.getPrice()*soldProduct.getQuantity(),saleResponse.getTotalAmount());

        assertEquals(50, saleResponse.getChange());

        //Testing UpdateStock method
        assertEquals(0,product.getStock());

    }

    @Test
    void testSale_IfTheBalanceIsInsufficient() {
        Product product=generateProduct();
        SoldProduct soldProduct=generateSoldProduct();

        SaleRequest saleRequest = new SaleRequest();
        List<SoldProductRequest> soldProductRequests = new ArrayList<>();
        SoldProductRequest soldProductRequest = new SoldProductRequest();
        soldProductRequest.setProductId(product.getId());
        soldProductRequest.setQuantity(soldProduct.getQuantity());
        soldProductRequests.add(soldProductRequest);
        saleRequest.setSoldProducts(soldProductRequests);
        saleRequest.setCashierName("Cebrail");
        saleRequest.setPaymentType("Cash");
        saleRequest.setReceivedMoney(3.0);

        Mockito.when(productRepository.findById(product.getId())).thenReturn(java.util.Optional.of(product));

        assertEquals(true,product.getPrice()*soldProduct.getQuantity()>saleRequest.getReceivedMoney());
        assertThrows(RuntimeException.class, () -> saleService.sale(saleRequest));

    }

    @Test
    void testCalculateDiscountForCampaignId_1L(){
        Product product = generateProduct();
        Campaign campaign = generateCampaign();


        long specificCampaignId = 1L;
        campaign.setId(specificCampaignId);

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.when(campaignRepository.findById(campaign.getId())).thenReturn(Optional.of(campaign));


        double discount = saleService.calculateDiscount(campaign, product.getPrice(), 3);

        double expectedDiscount = 0;

        if (campaign.getId() == specificCampaignId) {

            expectedDiscount = product.getPrice();
        }

        assertEquals(expectedDiscount, discount);
    }

    @Test
    void testCalculateDiscountForCampaignId_2L(){
        Product product = generateProduct();
        Campaign campaign = generateCampaign();


        long specificCampaignId = 2L;
        campaign.setId(specificCampaignId);

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.when(campaignRepository.findById(campaign.getId())).thenReturn(Optional.of(campaign));


        double discount = saleService.calculateDiscount(campaign, product.getPrice(), 3);

        double expectedDiscount = 0;

        if (campaign.getId() == specificCampaignId) {

            expectedDiscount = product.getPrice()*3*0.25;
        }

        assertEquals(expectedDiscount, discount);
    }
    @Test
    void testCalculateDiscountForCampaignId_3L(){
        Product product = generateProduct();
        Campaign campaign = generateCampaign();


        long specificCampaignId = 3L;
        campaign.setId(specificCampaignId);

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.when(campaignRepository.findById(campaign.getId())).thenReturn(Optional.of(campaign));


        double discount = saleService.calculateDiscount(campaign, product.getPrice(), 3);

        double expectedDiscount = 0;

        if (campaign.getId() == specificCampaignId) {
            int freeProductCount=1;
            expectedDiscount = product.getPrice()*freeProductCount;
        }

        assertEquals(expectedDiscount, discount);
    }

    @Test
    void testCalculateDiscountForCampaignId_4L(){
        Product product = generateProduct();
        Campaign campaign = generateCampaign();


        long specificCampaignId = 4L;
        campaign.setId(specificCampaignId);

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.when(campaignRepository.findById(campaign.getId())).thenReturn(Optional.of(campaign));


        double discount = saleService.calculateDiscount(campaign, product.getPrice(), 3);

        double expectedDiscount = 0;

        if (campaign.getId() == specificCampaignId) {
            int freeProductCount=1;
            expectedDiscount = product.getPrice()*3*0.10;
        }

        assertEquals(expectedDiscount, discount);
    }
}