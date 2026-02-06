package com.rodrigobarbosa.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rodrigobarbosa.order.domain.Customer;
import com.rodrigobarbosa.order.domain.Order;
import com.rodrigobarbosa.order.domain.OrderItem;
import com.rodrigobarbosa.order.domain.OrderStatus;
import com.rodrigobarbosa.order.repo.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.ErrorResponseException;

class OrderStatusTransitionsTest {

  @Test
  void updateStatus_created_to_preparing_ok() {
    OrderRepository repo = mock(OrderRepository.class);
    OrderService service = new OrderServiceImpl(repo, null);

    Order order = baseOrder(OrderStatus.CREATED);
    when(repo.findById("o1")).thenReturn(Optional.of(order));
    when(repo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

    service.updateStatus("o1", OrderStatus.PREPARING);

    verify(repo).save(any(Order.class));
  }

  @Test
  void updateStatus_delivered_to_cancelled_invalid_throwsConflict() {
    OrderRepository repo = mock(OrderRepository.class);
    OrderService service = new OrderServiceImpl(repo, null);

    Order order = baseOrder(OrderStatus.DELIVERED);
    when(repo.findById("o1")).thenReturn(Optional.of(order));

    assertThatThrownBy(() -> service.updateStatus("o1", OrderStatus.CANCELLED))
        .isInstanceOf(ErrorResponseException.class)
        .satisfies(
            ex -> {
              ErrorResponseException e = (ErrorResponseException) ex;
              assertThat(e.getStatusCode().value()).isEqualTo(409);
            });

    verify(repo, never()).save(any());
  }

  private static Order baseOrder(OrderStatus status) {
    Instant now = Instant.now();
    BigDecimal totalAmount = new BigDecimal("19.98");
    return new Order(
        "o1",
        new Customer("Taylor Jordan", "123 Main St, Springfield", "taylor.jordan@example.com"),
        List.of(
            new OrderItem("p1", "Margherita Pizza", new BigDecimal("9.99"), 1),
            new OrderItem("p2", "Caesar Salad", new BigDecimal("9.99"), 1)),
        totalAmount,
        status,
        now,
        now);
  }
}
