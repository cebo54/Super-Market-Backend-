package com.Toyota.rapor.service.concrete;



import com.Toyota.rapor.dao.SaleRepository;
import com.Toyota.rapor.dto.response.DetailsResponse;
import com.Toyota.rapor.dto.response.ReportResponse;
import com.Toyota.rapor.entity.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {

    private EntityManager entityManager;
    private SaleRepository saleRepository;

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        entityManager= Mockito.mock(EntityManager.class);
        saleRepository=Mockito.mock(SaleRepository.class);
        reportService=new ReportServiceImpl(entityManager,saleRepository);
    }
    public Sale generateSale() {
        List<SoldProduct> soldProducts = generateSoldProduct(); // Generate a list of SoldProduct
        return Sale.builder()
                .id(1L)
                .paymentDate(null)
                .cashierName("cebrail")
                .paymentType("cash")
                .receivedMoney(500)
                .change(200)
                .totalAmount(300)
                .soldProducts(soldProducts) // Assign the list of SoldProduct directly
                .build();
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
    public List<SoldProduct> generateSoldProduct() {
        SoldProduct soldProduct = SoldProduct.builder()
                .product(generateProduct())
                .quantity(5)
                .campaign(generateCampaign())
                .build();
        return Arrays.asList(soldProduct); // Return a regular list
    }
        public Category generateCategory(){
            return Category.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .build();
        }
        public Campaign generateCampaign(){
            return  Campaign.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .category(generateCategory())
                .build();
        }
    public static String convertPDFToString(byte[] pdfContent) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfContent))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
    @Test
    void shouldReturnAllReportsWithReportResponse() {
        int page=0;
        int size=10;
        String sortBy="id";
        String filter="filter";
        Sale sale1=generateSale();
        Sale sale2=generateSale();
        List<Sale> saleList= Arrays.asList(sale1,sale2);
        Pageable pageable= PageRequest.of(page,size, Sort.by(sortBy));
        PageImpl<Sale>pageImpl=new PageImpl<>(saleList,pageable,saleList.size());

        when(saleRepository.findAllWithFilter(any(Pageable.class),eq(filter))).thenReturn(pageImpl);
        List<ReportResponse>reportResponses=reportService.getReports(page,size,sortBy,filter);
        verify(saleRepository,times(1)).findAllWithFilter(any(Pageable.class),eq(filter));
        assertEquals(saleList.size(),reportResponses.size());
    }

    @Test
    void shouldReturnOneReportWithReportResponse_whenIdExist() {
        Long id=1L;
        Sale sale=generateSale();
        sale.setId(id);

        when(saleRepository.findById(id)).thenReturn(Optional.of(sale));

        ReportResponse reportResponse=reportService.getOneReport(id);

        verify(saleRepository,times(1)).findById(id);
        assertEquals(sale.getPaymentType(),reportResponse.getPaymentType());
        assertEquals(sale.getCashierName(),reportResponse.getCashierName());
        assertEquals(sale.getReceivedMoney(),reportResponse.getReceivedMoney());

    }

    @Test
    void shouldReturnDetailsOfSaleOnTheDesiredDay() {
        LocalDateTime dateTime=LocalDateTime.now();
        List<Sale>saleList=new ArrayList<>();
        Sale sale1=generateSale();
        sale1.setPaymentDate(dateTime);
        sale1.setPaymentType("Credit Card");
        List<SoldProduct> soldProducts1 = new ArrayList<>(generateSoldProduct()); // Create a new ArrayList
        soldProducts1.get(0).setQuantity(2);
        Product product1 = new Product();
        product1.setPrice(50.0);
        soldProducts1.get(0).setProduct(product1);
        sale1.setSoldProducts(soldProducts1);
        saleList.add(sale1);

        Sale sale2 = generateSale();
        sale2.setPaymentDate(dateTime);
        sale2.setPaymentType("cash");
        List<SoldProduct> soldProducts2 = new ArrayList<>(generateSoldProduct()); // Create a new ArrayList
        soldProducts2.get(0).setQuantity(3);
        Product product2 = generateProduct();
        product2.setPrice(30.0);
        soldProducts2.get(0).setProduct(product2);
        sale2.setSoldProducts(soldProducts2);
        saleList.add(sale2);

        when(saleRepository.findByPaymentDateAfter(dateTime)).thenReturn(saleList);

        DetailsResponse response=reportService.details(dateTime);

        verify(saleRepository,times(1)).findByPaymentDateAfter(dateTime);
        assertEquals(2,response.getTotalSales());
        assertEquals(190,response.getTotalAmount());
        assertEquals(1,response.getCreditCardPayments());
        assertEquals(1,response.getCashPayments());

    }

    @Test
    public void testGeneratePdfBySaleId() throws IOException {
        // Arrange
        Long saleId = 1L;
        Sale sale = generateSale();
        sale.setPaymentDate(LocalDateTime.now());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Mock the entityManager behavior
        when(entityManager.find(Sale.class, saleId)).thenReturn(sale);

        // Act
        try {
            reportService.generatePdfBySaleId(saleId, outputStream);
            byte[] pdfContent = outputStream.toByteArray();
            String s = convertPDFToString(pdfContent);

            // Assert
            assertNotNull(pdfContent);

            // Check for Payment Date
           assertTrue(s.contains("Payment Date: "+sale.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

            // Check for Sale ID
            assertTrue(s.contains("Sale ID: "+sale.getId()));

            // Check for Cashier Name
            assertTrue(s.contains("Cashier Name: "+sale.getCashierName()));

            // Check for Payment Type
            assertTrue(s.contains("Payment Type: "+sale.getPaymentType()));

            // Check for each sold product
            for (SoldProduct soldProduct : sale.getSoldProducts()) {
                assertTrue(s.contains("Product Name: "+soldProduct.getProduct().getName()));
                assertTrue(s.contains("Price: " + soldProduct.getProduct().getPrice()));
                assertTrue(s.contains("Quantity: " + soldProduct.getQuantity()));
                double totalPrice = soldProduct.getProduct().getPrice() * soldProduct.getQuantity();
                assertTrue(s.contains("Total Price: " + totalPrice));
            }

            // Check for Received Money, Change, and Total Amount
            assertTrue(s.contains("Received Money: "+sale.getReceivedMoney()));
            assertTrue(s.contains("Change: " + sale.getChange()));
            assertTrue(s.contains("Total Amount: " + sale.getTotalAmount()));
        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }
    @Test
    void testGeneratePdfBySaleId_saleNotFound() throws IOException {
        // Mock data
        Long saleId = 1L;
        Sale sale=generateSale();
        sale.setId(saleId);

        // Mock entityManager behavior
        when(entityManager.find(Sale.class, saleId)).thenReturn(null);

        // Create outputStream for testing
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Call the method
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reportService.generatePdfBySaleId(saleId, outputStream);
        });

        // Assertions
        assertEquals("Sale with ID " + saleId + " not found.", exception.getMessage());
    }


}