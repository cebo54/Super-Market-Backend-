package com.Toyota.rapor.service.concrete;


import com.Toyota.rapor.dao.SaleRepository;
import com.Toyota.rapor.dto.response.DetailsResponse;
import com.Toyota.rapor.dto.response.ReportResponse;
import com.Toyota.rapor.entity.Sale;
import com.Toyota.rapor.entity.SoldProduct;
import com.Toyota.rapor.service.Abstract.ReportService;


import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Service implementation for handling report-related operations.
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = Logger.getLogger(ReportServiceImpl.class);
    private final EntityManager entityManager;
    private final SaleRepository saleRepository;

    /**
     * Fetches a paginated and sorted list of report responses, filtered by the provided filter.
     *
     * @param page   The page number to fetch.
     * @param size   The size of the page.
     * @param sortBy The field to sort by.
     * @param filter The filter criteria.
     * @return A list of {@link ReportResponse} objects.
     */
    @Override
    public List<ReportResponse> getReports(Integer page, Integer size, String sortBy, String filter) {
        logger.info("Fetching all products");
        Pageable pageable= PageRequest.of(page,size, Sort.by(sortBy));
        Page<Sale> sales=saleRepository.findAllWithFilter(pageable,filter);
        List<ReportResponse>reportResponses=sales.stream().map(ReportResponse::convert).collect(Collectors.toList());
        logger.info("All products fetched successfully");
        return reportResponses;
    }
    /**
     * Fetches a single report response by its ID.
     *
     * @param id The ID of the report to fetch.
     * @return A {@link ReportResponse} object.
     */
    @Override
    public ReportResponse getOneReport(Long id) {
        logger.info("Fetching one report with ID " +id);
        Sale sale=saleRepository.findById(id).orElseThrow(()->new RuntimeException("Sale not found"));
        return ReportResponse.convert(sale);
    }

    /**
     * Fetches details of sales occurring after the specified payment date.
     *
     * @param paymentDate The payment date to filter sales.
     * @return A {@link DetailsResponse} object containing sales details.
     */
    @Override
    public DetailsResponse details(LocalDateTime paymentDate) {
        logger.info("Fetching report using payment date");
        List<Sale>sales=saleRepository.findByPaymentDateAfter(paymentDate);
        DetailsResponse detailsResponse=new DetailsResponse();

        for(Sale sale : sales){
            detailsResponse.incrementTotalSales();
            List<SoldProduct>soldProducts=sale.getSoldProducts();

            for (SoldProduct soldProduct :soldProducts){
                detailsResponse.incrementTotalSoldProducts(soldProduct.getQuantity());

                double productPrice=soldProduct.getProduct().getPrice();
                detailsResponse.addTotalAmount(productPrice * soldProduct.getQuantity());

            }
            if (sale.getPaymentType().equals("Credit Card")) {
                logger.info("Incremented the number of credit card payments");
                detailsResponse.incrementCreditCardPayments();
            } else if (sale.getPaymentType().equals("cash")) {
                logger.info("Incremented the number of cash payments");
                detailsResponse.incrementCashPayments();
            }
        }
        logger.info("Fetched the report on the requested date");
        return detailsResponse;
    }

    /**
     * Generates a PDF report for a sale by its ID.
     *
     * @param id           The ID of the sale.
     * @param outputStream The output stream to write the PDF to.
     * @throws IOException If an I/O error occurs during PDF generation.
     */

    @Transactional
    public void generatePdfBySaleId(Long id, OutputStream outputStream) throws IOException {
        Sale sale = entityManager.find(Sale.class, id);
        if (sale == null) {
            logger.error("Sale with ID " + id + " not found.");
            throw new IllegalArgumentException("Sale with ID " + id + " not found.");
        }

        List<SoldProduct> soldProducts = sale.getSoldProducts();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document pdfDocument = new Document(pdf)) {

            PdfFontFactory.registerDirectory("src/main/resources/fonts");
            pdfDocument.setFont(PdfFontFactory.createFont("fonts/MadimiOne-Regular.ttf"));

            // Payment Date, Sale ID, Cashier Name
            Paragraph paymentDate = new Paragraph()
                    .add(new Text("Payment Date: ").setBold())
                    .add(sale.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            Paragraph saleId = new Paragraph()
                    .add(new Text("Sale ID: ").setBold())
                    .add(String.valueOf(sale.getId()));
            Paragraph cashierName = new Paragraph()
                    .add(new Text("Cashier Name: ").setBold())
                    .add(sale.getCashierName());

            pdfDocument.add(paymentDate);
            pdfDocument.add(saleId);
            pdfDocument.add(cashierName);

            // Payment Type
            pdfDocument.add(new Paragraph()
                    .add(new Text("Payment Type: ").setBold())
                    .add(sale.getPaymentType()));

            // Sold Products
            pdfDocument.add(new Paragraph("\nSold Products:").setBold());
            for (SoldProduct soldProduct : soldProducts) {
                double totalPrice = soldProduct.getProduct().getPrice() * soldProduct.getQuantity();
                Paragraph productInfo = new Paragraph()
                        .add(new Text("Product Name: ").setBold())
                        .add(soldProduct.getProduct().getName())
                        .add(new Text(", Price: ").setBold())
                        .add(String.valueOf(soldProduct.getProduct().getPrice()))
                        .add(new Text(", Quantity: ").setBold())
                        .add(String.valueOf(soldProduct.getQuantity()))
                        .add(new Text(", Total Price: ").setBold())
                        .add(String.valueOf(totalPrice));
                pdfDocument.add(productInfo);
            }

            // Received Money, Change, Total Amount
            pdfDocument.add(new Paragraph("\nReceived Money: ")
                    .add(new Text(String.valueOf(sale.getReceivedMoney())).setBold()));
            pdfDocument.add(new Paragraph("Change: ")
                    .add(new Text(String.valueOf(sale.getChange())).setBold()));
            pdfDocument.add(new Paragraph("Total Amount: ")
                    .add(new Text(String.valueOf(sale.getTotalAmount())).setBold()));
        }
    }
}



