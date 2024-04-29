package com.Toyota.BackendProject.Dao;

import com.Toyota.BackendProject.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("select t from Product t where t.name like %:filter%")
    Page<Product> findAllWithFilter(Pageable pageable,@Param("filter") String filter);

    List<Product>findAllByCategoryId(Long id);


}
