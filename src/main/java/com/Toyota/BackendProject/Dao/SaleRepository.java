package com.Toyota.BackendProject.Dao;

import com.Toyota.BackendProject.Entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleRepository extends JpaRepository<Sale,Long> {
    @Query("select t from Sale t where t.cashierName like %:filter%")
    Page<Sale> findAllWithFilter(Pageable pageable, @Param("filter")String filter);
}
