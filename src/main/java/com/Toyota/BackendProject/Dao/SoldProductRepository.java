package com.Toyota.BackendProject.Dao;

import com.Toyota.BackendProject.Entity.SoldProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoldProductRepository extends JpaRepository<SoldProduct,Long> {
}
