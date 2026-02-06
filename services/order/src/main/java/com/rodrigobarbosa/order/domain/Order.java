package com.rodrigobarbosa.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
public class Order {
  @Id private String id;
  private Customer customer;
  private List<OrderItem> orderItems;
  private BigDecimal totalAmount;
  private OrderStatus orderStatus;
  private Instant createdAt;
  private Instant updatedAt;

  public Order() {}

  public Order(
      String id,
      Customer customer,
      List<OrderItem> orderItems,
      BigDecimal totalAmount,
      OrderStatus orderStatus,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.customer = customer;
    this.orderItems = orderItems;
    this.totalAmount = totalAmount;
    this.orderStatus = orderStatus;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public Customer getCustomer() {
    return customer;
  }

  public List<OrderItem> getOrderItems() {
    return orderItems;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public void setOrderItems(List<OrderItem> orderItems) {
    this.orderItems = orderItems;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
