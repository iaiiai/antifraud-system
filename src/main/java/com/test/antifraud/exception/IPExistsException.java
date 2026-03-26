package com.test.antifraud.exception;

public class IPExistsException extends RuntimeException {
  public IPExistsException(String message) {
    super(message);
  }
}
