package com.Toyota.BackendProject.dto;

import com.Toyota.BackendProject.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto {
    private String e_mail;
    private String name;
    private String username;
    private String password;

    private List<Long> role_id;
}
