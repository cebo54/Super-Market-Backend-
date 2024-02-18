package com.Toyota.BackendProject.dto.request;

import com.Toyota.BackendProject.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserViewRequest {

    private String email;
    private String name;
    private String username;
    private Boolean isActive;

    public static UserViewRequest convert(User user){
        return UserViewRequest.builder()
                .email(user.getEmail())
                .name(user.getName())
                .username(user.getUsername())
                .isActive(user.isActive())
                .build();
    }
    public static List<UserViewRequest> convertUserListToUserViewResponse(Page<User> users){
        return users.stream().map(UserViewRequest::convert).collect(Collectors.toList());
    }
}
