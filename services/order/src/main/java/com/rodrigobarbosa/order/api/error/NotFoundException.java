package com.rodrigobarbosa.order.api.error;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }

  public static NotFoundException order(String id) {
    return new NotFoundException("Order with id " + id + " not found");
  }
}
