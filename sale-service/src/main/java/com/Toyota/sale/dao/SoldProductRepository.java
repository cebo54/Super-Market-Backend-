package com.Toyota.sale.dao;


import com.Toyota.sale.entity.SoldProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoldProductRepository extends JpaRepository<SoldProduct,Long> {
}
