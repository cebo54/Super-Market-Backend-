package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.request.SaleRequest;
import com.Toyota.BackendProject.dto.response.SaleResponse;

public interface SaleService {
    SaleResponse sale(SaleRequest saleRequest);
}
