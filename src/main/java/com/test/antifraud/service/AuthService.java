package com.test.antifraud.service;

import com.test.antifraud.dto.request.CreateUserRequest;
import com.test.antifraud.dto.response.DeletedUserResponse;
import com.test.antifraud.dto.response.UserResponse;
import com.test.antifraud.exception.UserExistsException;
import com.test.antifraud.exception.UserNotFoundException;
import com.test.antifraud.mapper.UserMapper;
import com.test.antifraud.model.User;
import com.test.antifraud.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        User user = UserMapper.toEntity(createUserRequest);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserExistsException("Username is already taken.");
        }
        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    @Transactional
    public DeletedUserResponse deleteUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() == false) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteByUsername(username);
        return new DeletedUserResponse(user.get().getUsername());
    }

    public List<UserResponse> getAuthList() {
       return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
               .stream()
               .map(UserMapper::toResponse)
               .toList();
    }
}
