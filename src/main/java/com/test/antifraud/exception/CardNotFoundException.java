package com.test.antifraud.exception;

public class IPNotFoundException extends RuntimeException {
    public IPNotFoundException() {
        super("IP address not found");
    }
}
