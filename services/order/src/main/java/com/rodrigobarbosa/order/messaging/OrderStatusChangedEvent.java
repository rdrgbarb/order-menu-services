package com.rodrigobarbosa.order.messaging;

import java.time.Instant;

public record OrderStatusChangedEvent(
    String eventType,
    String orderId,
    String customerId, // using email as ID
    String customerName,
    String status,
    Instant occurredAt) {
  public static final String TYPE = "ORDER_STATUS_CHANGED";
}
