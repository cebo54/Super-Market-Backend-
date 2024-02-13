package com.Toyota.BackendProject.Dao;

import com.Toyota.BackendProject.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {


}
