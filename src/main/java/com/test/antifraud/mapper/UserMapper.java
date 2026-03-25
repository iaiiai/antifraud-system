package com.test.antifraud.mapper;

import com.test.antifraud.dto.request.CreateUserRequest;
import com.test.antifraud.dto.response.UserResponse;
import com.test.antifraud.model.User;

public class UserMapper {
    public static User toEntity(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(createUserRequest.getPassword());
        return user;
    }

    public static UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setUsername(user.getUsername());
        return userResponse;
    }
}
