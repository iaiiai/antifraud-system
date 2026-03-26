package com.test.antifraud.exception;
public class GlobalExceptionHandler{
public GlobalExceptionHandler() {
}
@org.springframework.web.bind.annotation.ExceptionHandler(com.test.antifraud.exception.UserExistsException.class)
    public org.springframework.http.ResponseEntity<com.test.antifraud.dto.base.ErrorResponse> handleUserExistsException(com.test.antifraud.exception.UserExistsException ex, org.springframework.web.context.request.WebRequest request) {
        org.springframework.http.HttpStatus status = org.springframework.http.HttpStatus.CONFLICT;
        com.test.antifraud.dto.base.ErrorResponse response = new com.test.antifraud.dto.base.ErrorResponse(
                ex.getMessage(),
                status.value(),
                java.lang.System.currentTimeMillis(),
                request.getDescription(false)
        );
        return new org.springframework.http.ResponseEntity<com.test.antifraud.dto.base.ErrorResponse>(response, status);
    }@org.springframework.web.bind.annotation.ExceptionHandler(com.test.antifraud.exception.UserNotFoundException.class)
    public org.springframework.http.ResponseEntity<com.test.antifraud.dto.base.ErrorResponse> handleUserNotFoundException(com.test.antifraud.exception.UserNotFoundException ex, org.springframework.web.context.request.WebRequest request) {
        org.springframework.http.HttpStatus status = org.springframework.http.HttpStatus.NOT_FOUND;
        com.test.antifraud.dto.base.ErrorResponse response = new com.test.antifraud.dto.base.ErrorResponse(
                ex.getMessage(),
                status.value(),
                java.lang.System.currentTimeMillis(),
                request.getDescription(false)
        );
        return new org.springframework.http.ResponseEntity<com.test.antifraud.dto.base.ErrorResponse>(response, status);
    }}