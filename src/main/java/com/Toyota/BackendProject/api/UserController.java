package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.Util.GenericResponse;
import com.Toyota.BackendProject.dto.request.RegisterDto;
import com.Toyota.BackendProject.dto.request.UserUpdateDto;
import com.Toyota.BackendProject.dto.request.UserViewDto;
import com.Toyota.BackendProject.dto.request.UserViewRequest;
import com.Toyota.BackendProject.dto.response.UserResponse;
import com.Toyota.BackendProject.service.Abstract.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getUsers")
    public GenericResponse<List<UserViewRequest>> getUsers(@RequestParam(defaultValue = "2",required = false)Integer isActive,
                                                           @RequestParam(defaultValue = "0",name = "page")Integer page,
                                                           @RequestParam(defaultValue = "0",name = "size") Integer size,
                                                           @RequestParam(defaultValue = "id",name = "sortBy")String sortBy,
                                                           @RequestParam(defaultValue = "",name = "filter")String filter)
    {
         List<UserViewRequest>users;
         users=userService.getUsers(isActive,page,size,sortBy,filter);

        return GenericResponse.successResult(users,"success.message.successful");
    }

    @PostMapping("/save")
    public GenericResponse<UserResponse> save(@RequestBody RegisterDto registerDto){

        return GenericResponse.successResult(userService.save(registerDto),"success.message.dataSaved");


    }
    @PutMapping("/update/{id}")
    public GenericResponse<UserViewDto>updateUser(@PathVariable Long id , @RequestBody UserUpdateDto userUpdateDto){
        try {
            final UserViewDto user = userService.updateUser(id, userUpdateDto);
            return GenericResponse.successResult(user, "success.message.dataUpdate");
        }catch (RuntimeException e){
            return GenericResponse.errorResult(e.getMessage());
        }
    }
    @PostMapping("/changeStatus/{id}")
    public GenericResponse<?>changeStatus(@PathVariable Long id){
        try {
            userService.changeStatus(id);
            return GenericResponse.success("success.message.status");
        }catch (RuntimeException e){
            return GenericResponse.errorResult(e.getMessage());
        }
    }

    @PostMapping("/roles/{id}")
    public GenericResponse<?>addRole(@PathVariable Long id ,@RequestParam String roleName){
        try {
            userService.addRole(id, roleName);
            return GenericResponse.success("success.message.roleAdd");
        }catch(RuntimeException e){
            return GenericResponse.errorResult(e.getMessage());
        }
    }
    @DeleteMapping("/roles/{id}")
    public GenericResponse<?>removeRole(@PathVariable Long id,@RequestParam String roleName){
        try {
            userService.removeRole(id, roleName);
            return GenericResponse.success("success.message.roleDel");
        }catch (RuntimeException e){
            return GenericResponse.errorResult(e.getMessage());
        }
    }



}
