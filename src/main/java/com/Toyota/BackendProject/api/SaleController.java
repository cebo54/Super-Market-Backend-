package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.dto.request.SaleRequest;
import com.Toyota.BackendProject.dto.response.SaleResponse;
import com.Toyota.BackendProject.service.Abstract.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sale")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<Object>sale(@RequestBody SaleRequest saleRequest){
        try {
            SaleResponse saleResponse = saleService.sale(saleRequest);
            return ResponseEntity.ok(saleResponse);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
