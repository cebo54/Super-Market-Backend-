package com.Toyota.User.dao;


import com.Toyota.User.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("select t from User t where t.isActive = :isActive and t.name like %:filter%")
    Page<User> findByIsActiveWithFilter(@Param("isActive") Boolean isActive, Pageable pageable, @Param("filter") String filter);

}
