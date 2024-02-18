package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.RoleRepository;
import com.Toyota.BackendProject.Dao.UserRepository;
import com.Toyota.BackendProject.Entity.Role;
import com.Toyota.BackendProject.Entity.User;
import com.Toyota.BackendProject.dto.request.RegisterDto;
import com.Toyota.BackendProject.dto.request.UserUpdateDto;
import com.Toyota.BackendProject.dto.request.UserViewDto;
import com.Toyota.BackendProject.dto.request.UserViewRequest;
import com.Toyota.BackendProject.dto.response.UserResponse;
import com.Toyota.BackendProject.security.JwtService;
import com.Toyota.BackendProject.service.Abstract.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;



import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger=Logger.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse save(RegisterDto registerDto) {

        List<Role> roles = registerDto.getRole_id().stream().map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        User user= User.builder().email(registerDto.getEmail())
                .name(registerDto.getName())
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(roles).build();
        user.setActive(true);

        userRepository.save(user);

        var token=jwtService.generateToken(user);
        logger.info(String.format("Registered successfully"));

        return UserResponse.builder().token(token).build();
    }

    @Override
    public List<UserViewRequest> getUsers(Integer isActive ,Integer page , Integer size,String sortBy,String filter) {

        Pageable pageable= PageRequest.of(page,size,Sort.by(sortBy));
        Boolean[] bools = {false, true};
        boolean isActiveStatus = bools[isActive];
        Page<User> finalRequest;
        if (filter.isEmpty()){finalRequest = userRepository.findByIsActive(isActiveStatus,pageable);}
        else {finalRequest = userRepository.findByIsActiveWithFilter(isActiveStatus,pageable,filter);}
        return UserViewRequest.convertUserListToUserViewResponse(finalRequest);
    }


    @Override
    public void addRole(Long id, String roleName) {
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        Role role=roleRepository.findByName(roleName);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRole(Long id, String roleName) {
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        Role role=roleRepository.findByName(roleName);
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    public UserViewDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        final User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        user.setEmail(userUpdateDto.getEmail());
        user.setName(userUpdateDto.getName());
        user.setUsername(userUpdateDto.getUsername());
        user.setPassword(userUpdateDto.getPassword());
        final User updatedUser=userRepository.save(user);
        logger.info("User updated successfully");
        return UserViewDto.convert(updatedUser);
    }

    @Override
    public void changeStatus(Long id) {
        Optional<User>optionalUser=userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            user.setActive(!user.isActive());
            userRepository.save(user);
        }
        else{
            throw new RuntimeException("User not found with id "+id);
        }
    }


}
