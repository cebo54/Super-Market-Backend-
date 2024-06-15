package com.Toyota.sale.service.Abstract;


import com.Toyota.sale.dto.request.SaleRequest;
import com.Toyota.sale.dto.response.SaleResponse;

public interface SaleService {
    SaleResponse sale(SaleRequest saleRequest);
}
