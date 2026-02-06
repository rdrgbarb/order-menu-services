package com.rodrigobarbosa.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rodrigobarbosa.order.api.dto.CreateOrderRequest;
import com.rodrigobarbosa.order.domain.Order;
import com.rodrigobarbosa.order.external.menu.MenuClient;
import com.rodrigobarbosa.order.repo.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.ErrorResponseException;

class OrderServiceTest {

  @Test
  void create_shouldSnapshotNamePrice_andComputeTotalAmount() {
    OrderRepository repo = mock(OrderRepository.class);
    MenuClient menuClient = mock(MenuClient.class);

    when(menuClient.getMenuItem("abc123"))
        .thenReturn(
            Optional.of(
                new MenuClient.MenuItem("abc123", "Pizza Margherita", new BigDecimal("12.50"))));
    when(menuClient.getMenuItem("def456"))
        .thenReturn(
            Optional.of(new MenuClient.MenuItem("def456", "Coca-Cola", new BigDecimal("3.00"))));

    when(repo.save(any(Order.class)))
        .thenAnswer(
            invocation -> {
              Order orderMock = invocation.getArgument(0);
              return new Order(
                  "order123",
                  orderMock.getCustomer(),
                  orderMock.getOrderItems(),
                  orderMock.getTotalAmount(),
                  orderMock.getOrderStatus(),
                  orderMock.getCreatedAt(),
                  orderMock.getUpdatedAt());
            });

    OrderService orderService = new OrderServiceImpl(repo, menuClient);

    CreateOrderRequest request =
        new CreateOrderRequest(
            new CreateOrderRequest.CustomerRequest("John Doe", "123 Main St", "john@example.com"),
            List.of(
                new CreateOrderRequest.CreateOrderItemRequest("abc123", 2),
                new CreateOrderRequest.CreateOrderItemRequest("def456", 3)));

    Order saved = orderService.create(request);

    assertThat(saved.getId()).isEqualTo("order123");
    assertThat(saved.getCustomer().getFullName()).isEqualTo("John Doe");
    assertThat(saved.getOrderItems()).hasSize(2);
    assertThat(saved.getOrderItems().getFirst().getPrice()).isEqualByComparingTo("12.50");
    assertThat(saved.getOrderItems().getFirst().getName()).isEqualTo("Pizza Margherita");
    assertThat(saved.getTotalAmount()).isEqualByComparingTo("34.00"); // 2 * 12.50 + 3 * 3.00

    verify(repo, times(1)).save(any(Order.class));
  }

  @Test
  void create_shouldThrow400_whenProductNotFoundInMenu() {
    OrderRepository repo = mock(OrderRepository.class);
    MenuClient menuClient = mock(MenuClient.class);

    when(menuClient.getMenuItem("invalid-product")).thenReturn(Optional.empty());

    OrderService orderService = new OrderServiceImpl(repo, menuClient);

    CreateOrderRequest request =
        new CreateOrderRequest(
            new CreateOrderRequest.CustomerRequest("John Doe", "123 Main St", "john@example.com"),
            List.of(new CreateOrderRequest.CreateOrderItemRequest("invalid-product", 1)));

    assertThatThrownBy(() -> orderService.create(request))
        .isInstanceOf(ErrorResponseException.class)
        .satisfies(
            ex -> {
              ErrorResponseException e = (ErrorResponseException) ex;
              assertThat(e.getStatusCode().value()).isEqualTo(400);
            });

    verify(repo, never()).save(any());
  }

  @Test
  void create_shouldThrow503_whenMenuUnavailable() {
    OrderRepository repo = mock(OrderRepository.class);
    MenuClient menuClient = mock(MenuClient.class);

    when(menuClient.getMenuItem("any-product"))
        .thenThrow(new MenuClient.MenuUnavailableException("Menu service is down"));

    OrderService orderService = new OrderServiceImpl(repo, menuClient);

    CreateOrderRequest request =
        new CreateOrderRequest(
            new CreateOrderRequest.CustomerRequest("John Doe", "123 Main St", "john@example.com"),
            List.of(new CreateOrderRequest.CreateOrderItemRequest("any-product", 1)));

    assertThatThrownBy(() -> orderService.create(request))
        .isInstanceOf(ErrorResponseException.class)
        .satisfies(
            ex -> {
              ErrorResponseException e = (ErrorResponseException) ex;
              assertThat(e.getStatusCode().value()).isEqualTo(503);
            });

    verify(repo, never()).save(any());
  }
}
