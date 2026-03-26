package com.test.antifraud.dto.response;

public record ErrorResponse(String message, int code, long timestamp, String path) {
}
