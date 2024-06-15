package com.Toyota.User.api;


import com.Toyota.User.dto.request.RegisterDto;
import com.Toyota.User.dto.request.UserUpdateDto;
import com.Toyota.User.dto.request.UserViewDto;
import com.Toyota.User.dto.request.UserViewRequest;
import com.Toyota.User.dto.response.UserResponse;
import com.Toyota.User.service.Abstract.UserService;
import com.Toyota.User.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * REST controller for managing users.
 * Provides endpoints for fetching, saving, updating users, changing user status,
 * and managing user roles.
 */

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);
    private final UserService userService;

    /**
     * Fetches a list of users with optional filters and pagination.
     *
     * @param isActive Specifies whether to fetch active users (1 for active, 0 for inactive).
     * @param page The page number to fetch.
     * @param size The size of the page to fetch.
     * @param sortBy The field to sort by.
     * @param filter Additional filter criteria.
     * @return A GenericResponse containing a list of UserViewRequest objects.
     */

    @GetMapping("/getUsers")
    public GenericResponse<List<UserViewRequest>> getUsers(@RequestParam(defaultValue = "1",required = false)Integer isActive,
                                                           @RequestParam(defaultValue = "0",name = "page")Integer page,
                                                           @RequestParam(defaultValue = "0",name = "size") Integer size,
                                                           @RequestParam(defaultValue = "id",name = "sortBy")String sortBy,
                                                           @RequestParam(defaultValue = "",name = "filter")String filter)
    {
        logger.info("Fetching users with filter: " + filter + ", sortBy: " + sortBy + ", page: " + page + ", size: " + size + ", isActive: " + isActive);
        List<UserViewRequest>users;
        users=userService.getUsers(isActive,page,size,sortBy,filter);
        logger.info("Users fetched successfully, total users: " + users.size());
        return GenericResponse.successResult(users,"success.message.successful");
    }

    /**
     * Saves a new user.
     *
     * @param registerDto The registration details of the new user.
     * @return A GenericResponse containing the details of the saved user.
     */
    @PostMapping("/save")
    public GenericResponse<UserResponse> save(@RequestBody RegisterDto registerDto){
        logger.info("Saving new user with details: " + registerDto);
        UserResponse userResponse = userService.save(registerDto);
        logger.info("User saved successfully with Name: " + userResponse.getName());
        return GenericResponse.successResult(userResponse, "success.message.dataSaved");
    }

    /**
     * Updates an existing user.
     *
     * @param id The ID of the user to update.
     * @param userUpdateDto The updated details of the user.
     * @return A GenericResponse containing the updated user details.
     */
    @PutMapping("/update/{id}")
    public GenericResponse<UserViewDto>updateUser(@PathVariable Long id , @RequestBody UserUpdateDto userUpdateDto){
        logger.info("Updating user with ID: " + id + " with details: " + userUpdateDto);
        try {
            final UserViewDto user = userService.updateUser(id, userUpdateDto);
            logger.info("User updated successfully with ID: " + id);
            return GenericResponse.successResult(user, "success.message.dataUpdate");
        }catch (RuntimeException e){
            logger.error("Error updating user with ID: " + id, e);
            return GenericResponse.errorResult("success.message.error");
        }
    }

    /**
     * Changes the status of a user (e.g., activate or deactivate).
     *
     * @param id The ID of the user whose status to change.
     * @return A GenericResponse indicating the result of the operation.
     */
    @PostMapping("/changeStatus/{id}")
    public GenericResponse<?>changeStatus(@PathVariable Long id){
        logger.info("Changing status for user with ID: " + id);
        try {
            userService.changeStatus(id);
            logger.info("User status changed successfully for user with ID: " + id);
            return GenericResponse.success("success.message.status");
        }catch (RuntimeException e){
            logger.error("Error changing status for user with ID: " + id, e);
            return GenericResponse.errorResult("success.message.error");
        }
    }

    /**
     * Adds a role to a user.
     *
     * @param id The ID of the user to whom the role will be added.
     * @param roleName The name of the role to add.
     * @return A GenericResponse indicating the result of the operation.
     */
    @PostMapping("/roles/{id}")
    public GenericResponse<?> addRole(@PathVariable Long id, @RequestParam String roleName) {
        logger.info("Adding role: " + roleName + " to user with ID: " + id);
        try {
            userService.addRole(id, roleName);
            logger.info("Role: " + roleName + " added successfully to user with ID: " + id);
            return GenericResponse.success("success.message.roleAdd");
        } catch (RuntimeException e) {
            logger.error("Error adding role: " + roleName + " to user with ID: " + id, e);
            if ("User already has the role".equals(e.getMessage())) {
                return GenericResponse.errorResult("success.message.error");
            }
            return GenericResponse.errorResult(e.getMessage());
        }
    }

    /**
     * Removes a role from a user.
     *
     * @param id The ID of the user from whom the role will be removed.
     * @param roleName The name of the role to remove.
     * @return A GenericResponse indicating the result of the operation.
     */
    @DeleteMapping("/roles/{id}")
    public GenericResponse<?>removeRole(@PathVariable Long id,@RequestParam String roleName){
        logger.info("Removing role: " + roleName + " from user with ID: " + id);
        try {
            userService.removeRole(id, roleName);
            logger.info("Role: " + roleName + " removed successfully from user with ID: " + id);
            return GenericResponse.success("success.message.roleDel");
        }catch (RuntimeException e){
            logger.error("Error removing role: " + roleName + " from user with ID: " + id, e);
            return GenericResponse.errorResult(e.getMessage());
        }
    }



}
