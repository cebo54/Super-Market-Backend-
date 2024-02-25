package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.Util.GenericResponse;
import com.Toyota.BackendProject.dto.request.SaleRequest;
import com.Toyota.BackendProject.dto.response.SaleResponse;
import com.Toyota.BackendProject.service.Abstract.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sale")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping("/makesale")
    public GenericResponse<SaleResponse> sale(@RequestBody SaleRequest saleRequest){
        try {
            SaleResponse saleResponse = saleService.sale(saleRequest);
            return GenericResponse.successResult(saleResponse,"success.message.successful");
        }catch (RuntimeException e){
            return GenericResponse.errorResult(e.getMessage());
        }
    }
}
