package com.Toyota.BackendProject.Dao;

import com.Toyota.BackendProject.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String roleName);
}
