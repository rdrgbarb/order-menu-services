package com.rodrigobarbosa.order.messaging;

public interface OrderEventPublisher {
  void publish(OrderStatusChangedEvent event);
}
