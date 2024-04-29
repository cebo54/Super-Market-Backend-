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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        userRepository= Mockito.mock(UserRepository.class);
        roleRepository=Mockito.mock(RoleRepository.class);
        jwtService=Mockito.mock(JwtService.class);
        passwordEncoder=Mockito.mock(PasswordEncoder.class);
        userService=new UserServiceImpl(userRepository,roleRepository,jwtService,passwordEncoder);
    }
    public User generateUser(){
        return User.builder()
                .id(1L)
                .email("email")
                .username("username")
                .name("name")
                .password("password")
                .roles(new ArrayList<>())
                .isActive(true)
                .build();
    }
    public User generateUser(Long id, boolean isActive) {
        return User.builder()
                .id(id)
                .email("email")
                .username("username")
                .name("name")
                .password("password")
                .roles(new ArrayList<>())
                .isActive(isActive)
                .build();
    }
    public Role generateRole(){
        return Role.builder()
                .id(1L)
                .name("ROLE_ADMIN")
                .users(new ArrayList<>())
                .build();
    }

    @Test
    void shouldSaveUserToDb() {
        // Mock data
        User user = generateUser();
        Role role = generateRole();

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(user.getEmail());
        registerDto.setUsername(user.getUsername());
        registerDto.setName(user.getName());
        registerDto.setPassword(user.getPassword());
        registerDto.setRole_id(Collections.singletonList(role.getId()));

        // Stub method calls
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("generatedToken");

        // Call the method
        UserResponse userResponse = userService.save(registerDto);

        // Verify method invocations
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verify(jwtService, Mockito.times(1)).generateToken(any(User.class));

        // Assertions
        assertNotNull(userResponse, "UserResponse should not be null");
        assertNotNull(userResponse.getToken(), "Token should not be null");
        assertEquals("generatedToken", userResponse.getToken(), "Token should match");
    }


    @Test
    void shouldReturnAllUsersWithUserViewRequest_whenNoFilter() {
        int page=0;
        int size=10;
        String sortBy="id";
        String filter="";
        int isActive=1;
        User user=generateUser();
        User user2=generateUser();
        User user3=generateUser();
        List<User>userList= Arrays.asList(user,user2,user3);

        Pageable pageable= PageRequest.of(page,size, Sort.by(sortBy));
        PageImpl<User>pageImpl=new PageImpl<>(userList,pageable,userList.size());

        when(userRepository.findByIsActive(true,pageable)).thenReturn(pageImpl);

        List<UserViewRequest>userViewRequestList=userService.getUsers(isActive,page,size,sortBy,filter);


        assertEquals(3,userList.size());
        assertEquals("name",userViewRequestList.get(0).getName());
        assertTrue(userViewRequestList.get(0).getIsActive());

    }
    @Test
    void shouldReturnAllUsersWithUserViewRequest_withFilter(){
        int page=0;
        int size=2;
        String sortBy="id";
        String filter="username";
        int isActive=1;
        User user=generateUser();
        User user2=generateUser();
        User user3=generateUser();
        List<User>userList= Arrays.asList(user,user2,user3);

        Pageable pageable= PageRequest.of(page,size, Sort.by(sortBy));
        PageImpl<User>pageImpl=new PageImpl<>(userList,pageable,userList.size());

        when(userRepository.findByIsActiveWithFilter(true,pageable,filter)).thenReturn(pageImpl);

        List<UserViewRequest>userViewRequestList=userService.getUsers(isActive,page,size,sortBy,filter);


        assertEquals(3,userList.size());
        assertEquals("name",userViewRequestList.get(0).getName());
        assertTrue(userViewRequestList.get(0).getIsActive());
    }

    @Test
    void shouldAddRoleToUser_whenIdExist() {
        Long id=1L;
        String roleName="ROLE_ADMIN";
        User user=generateUser();
        Role role=generateRole();
        role.setName(roleName);
        user.getRoles().add(role);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(role);

        userService.addRole(id,roleName);

        assertTrue(user.getRoles().contains(role));
        Mockito.verify(userRepository,Mockito.times(1)).save(user);


    }
    @Test
    void addRole_UserNotFound_ThrowsException() {
        // Mock data
        Long id = 1L;
        String roleName = "ROLE_ADMIN";

        // Stubbing repository methods
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Call the method and assert that it throws an exception
        assertThrows(RuntimeException.class, () -> userService.addRole(id, roleName));

        // Verify that userRepository.save(user) is never called
        Mockito.verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRemoveRoleFromUser_whenIdExist() {
        Long id=1L;
        String roleName="ROLE_ADMIN";
        User user=generateUser();
        Role role=generateRole();
        role.setName(roleName);
        user.getRoles().remove(role);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(role);

        userService.removeRole(id,roleName);

        assertFalse(user.getRoles().contains(role));
        verify(userRepository,times(1)).save(user);


    }
    @Test
    void removeRole_UserNotFound_ThrowsException() {
        // Mock data
        Long id = 1L;
        String roleName = "ROLE_ADMIN";

        // Stubbing repository methods
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Call the method and assert that it throws an exception
        assertThrows(RuntimeException.class, () -> userService.removeRole(id, roleName));

        // Verify that userRepository.save(user) is never called
        Mockito.verify(userRepository, never()).save(any());
    }

    @Test
    void shouldUpdateTheUserInformations_whenIdExist() {
        Long id = 1L;
        User user = generateUser();
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("cebo54@gmail.com");
        userUpdateDto.setName("cebrailkaya");
        userUpdateDto.setUsername("cebrail54");
        userUpdateDto.setPassword("newPassword");

        user.setEmail(userUpdateDto.getEmail());
        user.setName(userUpdateDto.getName());
        user.setUsername(userUpdateDto.getUsername());
        user.setPassword(userUpdateDto.getPassword());


        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserViewDto userViewDto = userService.updateUser(id, userUpdateDto);

        assertEquals(userUpdateDto.getName(), userViewDto.getName());
        assertEquals(userUpdateDto.getEmail(), userViewDto.getEmail());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(user); // Güncellenmiş user nesnesiyle save metodu çağrılmalı
    }

    @Test
    public void testUpdateNonExistingUser() {
        Long id = 1L;
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("newemail@example.com");
        userUpdateDto.setName("New Name");
        userUpdateDto.setUsername("newUsername");
        userUpdateDto.setPassword("newPassword");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.updateUser(id, userUpdateDto));
        verify(userRepository, times(1)).findById(id);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldChangeIsActiveFieldTrueToFalseOrFalseToTrue_whenIdExist() {
        Long id=1L;
        User user=generateUser(id,true);
        user.setId(id);
        boolean firstActive=user.isActive();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.changeStatus(id);

        assertEquals(!firstActive,user.isActive());
        verify(userRepository,times(1)).findById(id);
        verify(userRepository,times(1)).save(user);
    }
    @Test
    void shouldReturnErrorMessageForChangeStatusMethod_whenIdNotExist(){
        Long id=1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception=assertThrows(RuntimeException.class,()->userService.changeStatus(id));

        assertEquals("User not found with id " + id ,exception.getMessage());
        verify(userRepository,times(1)).findById(id);
        verify(userRepository,never()).save(any());

    }


    @AfterEach
    void tearDown() {
    }
}