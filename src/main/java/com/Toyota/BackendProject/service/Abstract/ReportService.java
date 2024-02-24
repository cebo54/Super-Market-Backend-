package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.response.ReportResponse;

import java.util.List;

public interface ReportService {
    List<ReportResponse> getReports(Integer page, Integer size, String sortBy, String filter);


    ReportResponse getOneReport(Long id);
}
