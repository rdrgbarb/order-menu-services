package com.rodrigobarbosa.order.api.dto;

import com.rodrigobarbosa.order.domain.Order;

public final class OrderMapper {

  private OrderMapper() {}

  public static OrderResponse toResponse(Order order) {
    return new OrderResponse(
        order.getId(),
        new OrderResponse.CustomerResponse(
            order.getCustomer().getFullName(),
            order.getCustomer().getAddress(),
            order.getCustomer().getEmail()),
        order.getOrderItems().stream()
            .map(
                it ->
                    new OrderResponse.OrderItemResponse(
                        it.getProductId(), it.getName(), it.getPrice(), it.getQuantity()))
            .toList(),
        order.getTotalAmount(),
        order.getOrderStatus().name(),
        order.getCreatedAt(),
        order.getUpdatedAt());
  }
}
