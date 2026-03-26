package com.test.antifraud.service.impl;

import com.test.antifraud.dto.request.CreateUserRequest;
import com.test.antifraud.dto.request.UpdateUserLockRequest;
import com.test.antifraud.dto.request.UpdateUserRoleRequest;
import com.test.antifraud.dto.response.DeletedUserResponse;
import com.test.antifraud.dto.response.OperationStatusResponse;
import com.test.antifraud.dto.response.UserResponse;
import com.test.antifraud.exception.UserExistsException;
import com.test.antifraud.exception.UserNotFoundException;
import com.test.antifraud.mapper.UserMapper;
import com.test.antifraud.model.enums.Operation;
import com.test.antifraud.model.entity.User;
import com.test.antifraud.model.enums.UserRole;
import com.test.antifraud.repository.UserRepository;
import com.test.antifraud.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.MERCHANT);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserExistsException("Username is already taken.");
        }
        Long nextId = userRepository.getMaxId() + 1;
        if (nextId == 1L) {
            user.setActive(true);
            user.setRole(UserRole.ADMINISTRATOR);
        }
        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    @Transactional
    public OperationStatusResponse updateUserLock(UpdateUserLockRequest request) {
        User user = userRepository
                .findByUsername(request.username())
                .orElseThrow(UserNotFoundException::new);
        boolean active = request.operation().name().equals(Operation.UNLOCK.name());
        user.setActive(active);
        userRepository.save(user);
        return new OperationStatusResponse("User " + user.getName() + " field active set to: " + active);
    }

    @Transactional
    public UserResponse updateUserRole(UpdateUserRoleRequest request) {
        User user = userRepository
                .findByUsername(request.username())
                .orElseThrow(UserNotFoundException::new);
        user.setRole(request.role());
        userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    @Transactional
    public DeletedUserResponse deleteUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() == false) {
            throw new UserNotFoundException();
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
