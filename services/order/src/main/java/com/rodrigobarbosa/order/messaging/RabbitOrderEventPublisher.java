package com.rodrigobarbosa.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitOrderEventPublisher implements OrderEventPublisher {

  private static final Logger log = LoggerFactory.getLogger(RabbitOrderEventPublisher.class);

  private final RabbitTemplate rabbitTemplate;
  private final String exchange;
  private final String routingKey;

  public RabbitOrderEventPublisher(
      RabbitTemplate rabbitTemplate,
      @Value("${app.rabbit.exchange}") String exchange,
      @Value("${app.rabbit.routing-key}") String routingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.routingKey = routingKey;
  }

  @Override
  public void publish(OrderStatusChangedEvent event) {
    rabbitTemplate.convertAndSend(exchange, routingKey, event);
    log.info(
        "Published eventType={} orderId={} status={}",
        event.eventType(),
        event.orderId(),
        event.status());
  }
}
