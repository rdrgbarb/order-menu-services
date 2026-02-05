package com.rodrigobarbosa.order.api.error;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
