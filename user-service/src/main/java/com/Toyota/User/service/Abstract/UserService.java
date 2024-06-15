package com.Toyota.User.service.Abstract;



import com.Toyota.User.dto.request.RegisterDto;
import com.Toyota.User.dto.request.UserUpdateDto;
import com.Toyota.User.dto.request.UserViewDto;
import com.Toyota.User.dto.request.UserViewRequest;
import com.Toyota.User.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse save(RegisterDto registerDto);

    UserViewDto updateUser(Long id, UserUpdateDto userUpdateDto);

    void changeStatus(Long id);

    List<UserViewRequest> getUsers(Integer isActive, Integer page, Integer size, String sortBy, String filter);


    void addRole(Long id, String roleName);

    void removeRole(Long id, String roleName);
}
