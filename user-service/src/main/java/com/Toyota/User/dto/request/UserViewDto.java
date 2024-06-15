package com.Toyota.User.dto.request;

import com.Toyota.User.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserViewDto implements Serializable {
    private static final long serialVersionUID=1L;

    private String email;
    private String name;
    private String username;
    private String password;

    public static UserViewDto convert(User user){
        return UserViewDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

}
