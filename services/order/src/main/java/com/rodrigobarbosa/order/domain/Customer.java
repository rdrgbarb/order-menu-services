package com.rodrigobarbosa.order.domain;

public class Customer {
  private String fullName;
  private String address;
  private String email;

  public Customer() {}

  public Customer(String fullName, String address, String email) {
    this.fullName = fullName;
    this.address = address;
    this.email = email;
  }

  public String getFullName() {
    return fullName;
  }

  public String getAddress() {
    return address;
  }

  public String getEmail() {
    return email;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
