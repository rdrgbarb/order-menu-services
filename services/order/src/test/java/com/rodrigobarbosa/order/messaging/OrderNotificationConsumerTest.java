package com.rodrigobarbosa.order.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class OrderNotificationConsumerTest {

  private final OrderNotificationConsumer consumer = new OrderNotificationConsumer();

  @Test
  void onMessage_logsNotification(CapturedOutput output) {
    var event =
        new OrderStatusChangedEvent(
            OrderStatusChangedEvent.TYPE,
            "order-123",
            "ze.pequeno@city.god",
            "Ze Pequeno",
            "PREPARING",
            Instant.parse("2026-02-20T10:15:30.00Z"));

    consumer.onMessage(event);

    assertThat(output.getOut())
        .contains("NOTIFICATION:")
        .contains("order-123")
        .contains("Ze Pequeno")
        .contains("PREPARING")
        .contains("2026-02-20T10:15:30Z");
  }
}
