package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.dto.request.RegisterDto;
import com.Toyota.BackendProject.dto.request.UserUpdateDto;
import com.Toyota.BackendProject.dto.request.UserViewDto;
import com.Toyota.BackendProject.dto.request.UserViewRequest;
import com.Toyota.BackendProject.dto.response.UserResponse;
import com.Toyota.BackendProject.service.Abstract.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getUsers")
    public ResponseEntity<List<UserViewRequest>>getUsers(@RequestParam(defaultValue = "2",required = false)Integer isActive,
                                                         @RequestParam(defaultValue = "0",name = "page")Integer page,
                                                         @RequestParam(defaultValue = "0",name = "size") Integer size,
                                                         @RequestParam(defaultValue = "id",name = "sortBy")String sortBy,
                                                         @RequestParam(defaultValue = "",name = "filter")String filter)
    {
         List<UserViewRequest>users;
           users=userService.getUsers(isActive,page,size,sortBy,filter);

        return ResponseEntity.ok(users);
    }

    @PostMapping("/save")
    public ResponseEntity<UserResponse> save(@RequestBody RegisterDto registerDto){

        return ResponseEntity.ok(userService.save(registerDto));


    }
    @PostMapping("/update/{id}")
    public ResponseEntity<UserViewDto>updateUser(@PathVariable Long id , @RequestBody UserUpdateDto userUpdateDto){
        final UserViewDto user=userService.updateUser(id,userUpdateDto);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/changeStatus/{id}")
    public ResponseEntity<?>changeStatus(@PathVariable Long id){
        try {
            userService.changeStatus(id);
            return ResponseEntity.ok("Status changed succesfully");
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/roles/{id}")
    public ResponseEntity<?>addRole(@PathVariable Long id ,@RequestParam String roleName){
        userService.addRole(id,roleName);
        return ResponseEntity.ok("Role added succesfully");
    }
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?>removeRole(@PathVariable Long id,@RequestParam String roleName){
        userService.removeRole(id,roleName);
        return ResponseEntity.ok("Role deleted succesfully");
    }



}
