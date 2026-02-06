package com.rodrigobarbosa.order.service;

import com.rodrigobarbosa.order.api.dto.CreateOrderRequest;
import com.rodrigobarbosa.order.api.error.NotFoundException;
import com.rodrigobarbosa.order.domain.Customer;
import com.rodrigobarbosa.order.domain.Order;
import com.rodrigobarbosa.order.domain.OrderItem;
import com.rodrigobarbosa.order.domain.OrderStatus;
import com.rodrigobarbosa.order.external.menu.MenuClient;
import com.rodrigobarbosa.order.repo.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final MenuClient menuClient;

  public OrderServiceImpl(OrderRepository orderRepository, MenuClient menuClient) {
    this.orderRepository = orderRepository;
    this.menuClient = menuClient;
  }

  @Override
  public Order create(CreateOrderRequest request) {
    Instant now = Instant.now();

    Customer customer =
        new Customer(
            request.customer().fullName(),
            request.customer().address(),
            request.customer().email());

    final List<OrderItem> items;
    try {
      items =
          request.orderItems().stream()
              .map(
                  item -> {
                    MenuClient.MenuItem menuItem =
                        menuClient
                            .getMenuItem(item.productId())
                            .orElseThrow(
                                () -> badRequest("Invalid product ID: " + item.productId()));

                    return new OrderItem(
                        item.productId(), menuItem.name(), menuItem.price(), item.quantity());
                  })
              .toList();
    } catch (MenuClient.MenuUnavailableException e) {
      throw serviceUnavailable(e.getMessage());
    }
    BigDecimal totalAmount =
        items.stream()
            .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Order order = new Order(null, customer, items, totalAmount, OrderStatus.CREATED, now, now);

    return orderRepository.save(order);
  }

  @Override
  public Order getById(String id) {
    return orderRepository.findById(id).orElseThrow(() -> NotFoundException.order(id));
  }

  private static ErrorResponseException badRequest(String message) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    return new ErrorResponseException(HttpStatus.BAD_REQUEST, pd, null);
  }

  private static ErrorResponseException serviceUnavailable(String message) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, message);
    return new ErrorResponseException(HttpStatus.SERVICE_UNAVAILABLE, pd, null);
  }
}
