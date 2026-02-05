package com.rodrigobarbosa.menu.api.error;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }

  public static NotFoundException menuItem(String id) {
    return new NotFoundException("MenuItem with id " + id + " not found");
  }
}
