package com.rodrigobarbosa.order.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    String id,
    CustomerResponse customer,
    List<OrderItemResponse> orderItems,
    BigDecimal totalAmount,
    String orderStatus,
    Instant createdAt,
    Instant updatedAt) {
  public record CustomerResponse(String fullName, String address, String email) {}

  public record OrderItemResponse(String productId, String name, BigDecimal price, int quantity) {}
}
