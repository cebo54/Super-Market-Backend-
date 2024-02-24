package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.Util.GenericResponse;
import com.Toyota.BackendProject.dto.response.ReportResponse;
import com.Toyota.BackendProject.service.Abstract.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
