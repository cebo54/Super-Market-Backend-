package com.Toyota.BackendProject.Dao;

import com.Toyota.BackendProject.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findByIsActive(Boolean isActive,Pageable pageable);

    @Query("select t from Product t where t.isActive = :isActive and t.name like %:filter%")
    Page<Product> findByIsActiveWithFilter(@Param("isActive")Boolean isActive, Pageable pageable,@Param("filter") String filter);

    List<Product>findAllByCategoryId(Long id);


}
