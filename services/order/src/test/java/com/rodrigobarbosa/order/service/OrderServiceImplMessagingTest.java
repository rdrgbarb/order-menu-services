package com.rodrigobarbosa.order.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rodrigobarbosa.order.api.dto.UpdateOrderStatusRequest;
import com.rodrigobarbosa.order.domain.Customer;
import com.rodrigobarbosa.order.domain.Order;
import com.rodrigobarbosa.order.domain.OrderStatus;
import com.rodrigobarbosa.order.external.menu.MenuClient;
import com.rodrigobarbosa.order.messaging.OrderEventPublisher;
import com.rodrigobarbosa.order.repo.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplMessagingTest {

  @Mock OrderRepository repo;
  @Mock MenuClient menuClient;
  @Mock OrderEventPublisher publisher;

  @InjectMocks OrderServiceImpl service;

  @Test
  void updateStatus_publishesEvent() {
    Instant now = Instant.now();
    Order existing =
        new Order(
            "order123",
            new Customer("John Doe", "Street 1", "john@example.com"),
            List.of(),
            BigDecimal.ZERO,
            OrderStatus.CREATED,
            now,
            now);

    when(repo.findById("order123")).thenReturn(Optional.of(existing));
    when(repo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.updateStatus("order123", new UpdateOrderStatusRequest(OrderStatus.PREPARING));

    verify(publisher, times(1)).publish(any());
  }
}
