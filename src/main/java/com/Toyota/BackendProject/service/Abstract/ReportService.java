package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.response.DetailsResponse;
import com.Toyota.BackendProject.dto.response.ReportResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    List<ReportResponse> getReports(Integer page, Integer size, String sortBy, String filter);


    ReportResponse getOneReport(Long id);


    DetailsResponse details(LocalDateTime paymentDate);

    void generatePdfBySaleId(Long id, OutputStream outputStream) throws IOException;


}
