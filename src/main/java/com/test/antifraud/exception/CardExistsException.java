package com.test.antifraud.exception;

public class IPExistsException extends RuntimeException {
    public IPExistsException() {
        super("IP address already blacklisted");
    }
}
