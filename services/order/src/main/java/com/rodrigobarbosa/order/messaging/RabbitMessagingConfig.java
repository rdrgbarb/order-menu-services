package com.rodrigobarbosa.order.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RabbitMessagingConfig {

  @Bean
  public DirectExchange orderEventsExchange(@Value("${app.rabbit.exchange}") String exchange) {
    return new DirectExchange(exchange, true, false);
  }

  @Bean
  public Queue orderNotificationsQueue(@Value("${app.rabbit.queue}") String queue) {
    return new Queue(queue, true);
  }

  @Bean
  public Binding orderStatusChangedBinding(
      Queue orderNotificationsQueue,
      DirectExchange orderEventsExchange,
      @Value("${app.rabbit.routing-key}") String routingKey) {
    return BindingBuilder.bind(orderNotificationsQueue).to(orderEventsExchange).with(routingKey);
  }

  @Bean
  public MessageConverter jacksonMessageConverter() {
    return new JacksonJsonMessageConverter();
  }
}
