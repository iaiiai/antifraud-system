package com.test.antifraud.service;

import com.test.antifraud.dto.request.CreateUserRequest;
import com.test.antifraud.dto.request.UpdateUserLockRequest;
import com.test.antifraud.dto.request.UpdateUserRoleRequest;
import com.test.antifraud.dto.response.DeletedUserResponse;
import com.test.antifraud.dto.response.OperationStatusResponse;
import com.test.antifraud.dto.response.UserResponse;
import com.test.antifraud.exception.UserExistsException;
import com.test.antifraud.exception.UserNotFoundException;

import java.util.List;

public interface AuthService {

    /**
     * Creates a new user.
     *
     * @param request the request containing user details
     * @return the created user response
     * @throws UserExistsException if the username already exists
     */
    UserResponse createUser(CreateUserRequest request) throws UserExistsException;

    /**
     * Updates the lock status of a user.
     *
     * @param request the request containing username and operation
     * @return operation status response
     * @throws UserNotFoundException if the user is not found
     */
    OperationStatusResponse updateUserLock(UpdateUserLockRequest request) throws UserNotFoundException;

    /**
     * Updates the role of a user.
     *
     * @param request the request containing username and new role
     * @return updated user response
     * @throws UserNotFoundException if the user is not found
     */
    UserResponse updateUserRole(UpdateUserRoleRequest request) throws UserNotFoundException;

    /**
     * Deletes a user by username.
     *
     * @param username the username to delete
     * @return deleted user response
     * @throws UserNotFoundException if the user is not found
     */
    DeletedUserResponse deleteUser(String username) throws UserNotFoundException;

    /**
     * Retrieves the list of all users.
     *
     * @return list of user responses
     */
    List<UserResponse> getAuthList();
}