package com.test.antifraud.controller;

import com.test.antifraud.dto.request.CreateUserRequest;
import com.test.antifraud.dto.response.DeletedUserResponse;
import com.test.antifraud.dto.response.UserResponse;
import com.test.antifraud.dto.response.ErrorResponse;
import com.test.antifraud.exception.UserExistsException;
import com.test.antifraud.exception.UserNotFoundException;
import com.test.antifraud.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/user")
    @ResponseBody
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserResponse userResponse = this.authService.createUser(createUserRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<UserResponse>> getAuthList() {
        return new ResponseEntity<>(authService.getAuthList(), HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    @ResponseBody
    public ResponseEntity<DeletedUserResponse> deleteUser(@PathVariable String username) {
        return new ResponseEntity<>(authService.deleteUser(username), HttpStatus.OK);
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExistsException(UserExistsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                status.value(),
                System.currentTimeMillis(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                status.value(),
                System.currentTimeMillis(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, status);
    }
}
