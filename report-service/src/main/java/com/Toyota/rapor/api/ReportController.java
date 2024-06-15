package com.Toyota.rapor.api;


import com.Toyota.rapor.dto.response.DetailsResponse;
import com.Toyota.rapor.dto.response.ReportResponse;
import com.Toyota.rapor.service.Abstract.ReportService;
import com.Toyota.rapor.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/report")
public class ReportController {
    private static final Logger logger = Logger.getLogger(ReportController.class);
    private final ReportService reportService;


    /**
    * Request for list all reports with using pageable
    * @param page is do you want how many page to list
    * @param size is do you want how many report on the page
    * @param sortBy is how do you want the reports to be sorted
    * @param filter is do you want to filter reports
    * */
    @GetMapping("/getReports")
    public GenericResponse<List<ReportResponse>> getReports(@RequestParam(defaultValue = "0",name = "page")Integer page,
                                                            @RequestParam(defaultValue = "0",name = "size") Integer size,
                                                            @RequestParam(defaultValue = "id",name = "sortBy")String sortBy,
                                                            @RequestParam(defaultValue = "",name = "filter")String filter){
        logger.info("Fetching reports with pagination: page=" + page + ", size=" + size + ", sortBy=" + sortBy + ", filter=" + filter);
        List<ReportResponse>reports;
        reports=reportService.getReports(page,size,sortBy,filter);
        logger.info("Fetched " + reports.size() + " reports successfully");
        return GenericResponse.successResult(reports,"success.message.successful");
    }
    /**
     * Fetches a report by its ID.
     *
     * @param id The ID of the report to fetch.
     * @return A GenericResponse containing the report details if found, or an error message if not.
     */
    @GetMapping("/getOneReport/{id}")
    public GenericResponse<ReportResponse>getOneProduct(@PathVariable Long id){
        try {
            logger.info("Fetching report with ID: " + id);
            return GenericResponse.successResult(reportService.getOneReport(id), "success.message.successful");
        }catch (RuntimeException e){
            logger.error("Error occurred while fetching report with ID: " + id, e);
            return GenericResponse.errorResult("success.message.error");
        }
    }
    /**
     * Fetches details of sales based on the provided payment date.
     *
     * @param paymentDateStr The payment date string in the format "yyyy-MM-dd".
     * @return A GenericResponse containing the sales details if the date is valid, or an error message if not.
     */
    @GetMapping("/detailsOfSale")
    public GenericResponse<DetailsResponse>details(@RequestParam("paymentDateStr")
                                                       @DateTimeFormat(pattern="yyyy-MM-dd")String paymentDateStr){

        logger.info("Fetching details for sale on date: " + paymentDateStr);
        LocalDateTime dateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate date = LocalDate.parse(paymentDateStr, formatter);
            dateTime=date.atStartOfDay();
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date string: " + paymentDateStr, e);
            return GenericResponse.errorResult("success.message.error");
        }

        return GenericResponse.successResult(reportService.details(dateTime), "success.message.successful");

    }
    /**
     * Generates a PDF report for a sale by its ID.
     *
     * @param id The ID of the sale for which to generate the PDF report.
     * @return A ResponseEntity containing the PDF bytes if the report is generated successfully, or an error status if not.
     */
     @GetMapping("/{id}/pdf")
     public ResponseEntity<byte[]> generatePdfBySaleId(@PathVariable Long id) {
         logger.info("Generating PDF report for sale with ID: " + id);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         try {
             reportService.generatePdfBySaleId(id, outputStream);
             byte[] pdfBytes = outputStream.toByteArray();

             HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_PDF);
             headers.setContentDispositionFormData("filename", "sale_report.pdf");
             logger.info("PDF report generated successfully for sale with ID: " + id);
             return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
         } catch (IOException e) {
             logger.error("Error occurred while generating PDF report for sale with ID: " + id, e);
             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
}
