package com.rodrigobarbosa.order.domain;

public final class OrderStatusTransition {

  private OrderStatusTransition() {}

  public static boolean isAllowed(OrderStatus from, OrderStatus to) {
    if (from == null || to == null) {
      return false;
    }
    if (from == to) {
      return true;
    }
    return switch (from) {
      case CREATED -> (to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED);
      case PREPARING -> (to == OrderStatus.DELIVERED || to == OrderStatus.CANCELLED);
      case DELIVERED, CANCELLED -> false;
    };
  }
}
