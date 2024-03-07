package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.Util.GenericResponse;
import com.Toyota.BackendProject.dto.response.ReportResponse;
import com.Toyota.BackendProject.dto.response.DetailsResponse;
import com.Toyota.BackendProject.service.Abstract.ReportService;
import lombok.RequiredArgsConstructor;
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
    private final ReportService reportService;


    @GetMapping("/getReports")
    public GenericResponse<List<ReportResponse>> getReports(@RequestParam(defaultValue = "0",name = "page")Integer page,
                                                            @RequestParam(defaultValue = "0",name = "size") Integer size,
                                                            @RequestParam(defaultValue = "id",name = "sortBy")String sortBy,
                                                            @RequestParam(defaultValue = "",name = "filter")String filter){
        List<ReportResponse>reports;
        reports=reportService.getReports(page,size,sortBy,filter);

        return GenericResponse.successResult(reports,"success.message.successful");
    }

    @GetMapping("/getOneReport/{id}")
    public GenericResponse<ReportResponse>getOneProduct(@PathVariable Long id){
        try {
            return GenericResponse.successResult(reportService.getOneReport(id), "success.message.successful");
        }catch (RuntimeException e){
            return GenericResponse.errorResult(e.getMessage());
        }
    }

    @GetMapping("/detailsOfSale")
    public GenericResponse<DetailsResponse>details(@RequestParam("paymentDateStr")
                                                       @DateTimeFormat(pattern="yyyy-MM-dd")String paymentDateStr){

        LocalDateTime dateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate date = LocalDate.parse(paymentDateStr, formatter);
            dateTime=date.atStartOfDay();
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return GenericResponse.errorResult("success.message.error");
        }

        return GenericResponse.successResult(reportService.details(dateTime), "success.message.successful");

    }
     @GetMapping("/{id}/pdf")
     public ResponseEntity<byte[]> generatePdfBySaleId(@PathVariable Long id) {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         try {
             reportService.generatePdfOrBase64BySaleId(id, outputStream);
             byte[] pdfBytes = outputStream.toByteArray();

             HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_PDF);
             headers.setContentDispositionFormData("filename", "sale_report.pdf");

             return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
         } catch (IOException e) {
             e.printStackTrace();
             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
}
