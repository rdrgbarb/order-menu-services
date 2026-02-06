package com.rodrigobarbosa.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderNotificationConsumer {

  private static final Logger log = LoggerFactory.getLogger(OrderNotificationConsumer.class);

  @RabbitListener(queues = "${app.rabbit.queue}")
  public void onMessage(OrderStatusChangedEvent event) {
    // Simulating sending a notification (e.g., email, etc.) to the customer about the order status
    // change
    log.info(
        "NOTIFICATION: customer={} customerId={} orderId={} newStatus={} occurredAt={}",
        event.customerName(),
        event.customerId(),
        event.orderId(),
        event.status(),
        event.occurredAt());
  }
}
