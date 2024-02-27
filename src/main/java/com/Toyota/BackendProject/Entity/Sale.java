package com.Toyota.BackendProject.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="sale")
@Builder
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="paymentDate",length = 15,nullable = false)
    private LocalDateTime paymentDate;
    @Column(name="cashierName",length = 20,nullable = false)
    private String cashierName;
    @Column(name="paymentType",length = 20,nullable = false)
    private String paymentType;
    @Column(name="receivedMoney",length = 20,nullable = false)
    private double receivedMoney;

    @Column(name="change",length = 20)
    private double change;
    @Column(name="totalAmount",length = 20,nullable = false)
    private double totalAmount;


    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SoldProduct> soldProducts;




}
