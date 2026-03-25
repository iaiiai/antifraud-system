package com.test.antifraud.dto.response;

public record DeletedUserResponse(String username, String status) {
    public DeletedUserResponse(String username) {
        this(username, "Deleted successfully!");
    }
}
