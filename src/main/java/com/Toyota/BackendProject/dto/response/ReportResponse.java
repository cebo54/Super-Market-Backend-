package com.Toyota.BackendProject.dto.response;

import com.Toyota.BackendProject.Entity.Sale;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime date;
    private Long saleId;

    private String cashierName;

    private String paymentType;

    private List<SoldProductResponse2> soldProducts;

    private double totalAmount;
    private double receivedMoney;

    private double change;

    public static ReportResponse convert(Sale sale){
        List<SoldProductResponse2>soldProducts=sale.getSoldProducts().stream()
                .map(SoldProductResponse2::convert).collect(Collectors.toList());
        return ReportResponse.builder()
                .date(sale.getPaymentDate())
                .saleId(sale.getId())
                .cashierName(sale.getCashierName())
                .paymentType(sale.getPaymentType())
                .soldProducts(soldProducts)
                .totalAmount(sale.getTotalAmount())
                .receivedMoney(sale.getReceivedMoney())
                .change(sale.getChange())
                .build();
    }



}
