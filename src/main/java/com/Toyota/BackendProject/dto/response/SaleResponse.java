package com.Toyota.BackendProject.dto.response;

import com.Toyota.BackendProject.Entity.Sale;
import com.Toyota.BackendProject.Entity.SoldProduct;
import com.Toyota.BackendProject.dto.request.SaleRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponse {
    private Long sale_id;
    private LocalDateTime paymentDate;
    private String cashierName;

    private String paymentType;
    private List<SoldProductResponse>soldProductResponses;

    private double receivedMoney;

    private double change;

    private double totalAmount;


    public static SaleResponse convert(Sale sale,List<SoldProduct>soldProducts){
        List<SoldProductResponse>soldProductResponses=soldProducts.stream()
                .map(SoldProductResponse::convert).collect(Collectors.toList());

        return SaleResponse.builder()
                .sale_id(sale.getId())
                .paymentDate(sale.getPaymentDate())
                .cashierName(sale.getCashierName())
                .paymentType(sale.getPaymentType())
                .soldProductResponses(soldProductResponses)
                .receivedMoney(sale.getReceivedMoney())
                .change(sale.getChange())
                .totalAmount(sale.getTotalAmount())
                .build();

    }


}
