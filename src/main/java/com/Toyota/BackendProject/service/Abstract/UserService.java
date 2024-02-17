package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.request.RegisterDto;
import com.Toyota.BackendProject.dto.request.UserUpdateDto;
import com.Toyota.BackendProject.dto.request.UserViewDto;
import com.Toyota.BackendProject.dto.request.UserViewRequest;
import com.Toyota.BackendProject.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse save(RegisterDto registerDto);

    UserViewDto updateUser(Long id, UserUpdateDto userUpdateDto);

    void changeStatus(Long id);

    List<UserViewRequest> getUsers(Integer isActive, Integer page, Integer size, String sortBy, String filter);


    void addRole(Long id, String roleName);

    void removeRole(Long id, String roleName);
}
