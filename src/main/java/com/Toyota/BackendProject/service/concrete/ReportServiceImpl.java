package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.SaleRepository;
import com.Toyota.BackendProject.Entity.Sale;
import com.Toyota.BackendProject.Entity.SoldProduct;
import com.Toyota.BackendProject.dto.response.DetailsResponse;
import com.Toyota.BackendProject.dto.response.ReportResponse;
import com.Toyota.BackendProject.service.Abstract.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    @Override
    public List<ReportResponse> getReports(Integer page, Integer size, String sortBy, String filter) {
        Pageable pageable= PageRequest.of(page,size, Sort.by(sortBy));
        Page<Sale> sales=saleRepository.findAllWithFilter(pageable,filter);
        List<ReportResponse>reportResponses=sales.stream().map(ReportResponse::convert).collect(Collectors.toList());

        return reportResponses;
    }

    @Override
    public ReportResponse getOneReport(Long id) {
        Sale sale=saleRepository.findById(id).orElseThrow(()->new RuntimeException("Sale not found"));
        return ReportResponse.convert(sale);
    }

    @Override
    public DetailsResponse details(LocalDateTime paymentDate) {
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
                detailsResponse.incrementCreditCardPayments();
            } else if (sale.getPaymentType().equals("cash")) {
                detailsResponse.incrementCashPayments();
            }
        }
        return detailsResponse;
    }


}
