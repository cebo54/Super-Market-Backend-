package com.Toyota.User.service.concrete;


import com.Toyota.User.dao.RoleRepository;
import com.Toyota.User.dao.UserRepository;
import com.Toyota.User.dto.request.RegisterDto;
import com.Toyota.User.dto.request.UserUpdateDto;
import com.Toyota.User.dto.request.UserViewDto;
import com.Toyota.User.dto.request.UserViewRequest;
import com.Toyota.User.dto.response.UserResponse;
import com.Toyota.User.entity.Role;
import com.Toyota.User.entity.User;
import com.Toyota.User.service.Abstract.UserService;
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


/**
 * Service implementation for managing users.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger=Logger.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * Saves a new user with the provided registration details.
     *
     * @param registerDto The registration details of the new user.
     * @return A UserResponse containing the name of the saved user.
     */
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

        logger.info(String.format("Registered successfully"));

        return UserResponse.builder().name(user.getName()).build();
    }

    /**
     * Fetches a list of users with optional filters, pagination, and sorting.
     *
     * @param isActive Specifies whether to fetch active users (1 for active, 0 for inactive).
     * @param page The page number to fetch.
     * @param size The size of the page to fetch.
     * @param sortBy The field to sort by.
     * @param filter Additional filter criteria.
     * @return A list of UserViewRequest objects.
     */
    @Override
    public List<UserViewRequest> getUsers(Integer isActive , Integer page , Integer size, String sortBy, String filter) {

        Pageable pageable= PageRequest.of(page,size,Sort.by(sortBy));
        Boolean[] bools = {false, true};
        boolean isActiveStatus = bools[isActive];
        Page<User> finalRequest;
        if (filter.isEmpty()){finalRequest = userRepository.findByIsActive(isActiveStatus,pageable);}
        else {finalRequest = userRepository.findByIsActiveWithFilter(isActiveStatus,pageable,filter);}
        return UserViewRequest.convertUserListToUserViewResponse(finalRequest);
    }

    /**
     * Adds a role to a user.
     *
     * @param userId The ID of the user to whom the role will be added.
     * @param roleName The name of the role to add.
     */

    @Override
    public void addRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (user.getRoles().contains(role)) {
            throw new RuntimeException("User already has the role");
        }

        user.getRoles().add(role);
        userRepository.save(user);

        logger.info("Role added successfully: " + roleName + " to user: " + user.getUsername());
    }

    /**
     * Removes a role from a user.
     *
     * @param id The ID of the user from whom the role will be removed.
     * @param roleName The name of the role to remove.
     */
    @Override
    public void removeRole(Long id, String roleName) {
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        Role role=roleRepository.findByName(roleName).orElseThrow(()->new RuntimeException("Role not found"));

        user.getRoles().remove(role);
        userRepository.save(user);

        logger.info("Role removed successfully: " + roleName + " from user: " + user.getUsername());
    }

    /**
     * Updates the details of an existing user.
     *
     * @param id The ID of the user to update.
     * @param userUpdateDto The updated details of the user.
     * @return A UserViewDto containing the updated user details.
     */
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

    /**
     * Changes the status of a user (e.g., activate or deactivate).
     *
     * @param id The ID of the user whose status to change.
     */
    @Override
    public void changeStatus(Long id) {
        User user=userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found with id " + id));
        user.setActive(!user.isActive());
        userRepository.save(user);
        logger.info("User status changed successfully for user: " + user.getUsername());
    }


}
