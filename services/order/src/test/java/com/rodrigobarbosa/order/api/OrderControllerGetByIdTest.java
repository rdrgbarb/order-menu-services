package com.rodrigobarbosa.order.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rodrigobarbosa.order.api.error.GlobalExceptionHandler;
import com.rodrigobarbosa.order.api.error.NotFoundException;
import com.rodrigobarbosa.order.domain.Customer;
import com.rodrigobarbosa.order.domain.Order;
import com.rodrigobarbosa.order.domain.OrderItem;
import com.rodrigobarbosa.order.domain.OrderStatus;
import com.rodrigobarbosa.order.service.OrderService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerGetByIdTest {

  @Autowired private MockMvc mvc;
  @MockitoBean private OrderService orderService;

  @Test
  void getOrder_shouldReturn200_whenFound() throws Exception {
    String id = "123";
    Instant now = Instant.parse("2026-02-06T00:00:00Z");

    Order order =
        new Order(
            id,
            new Customer("John Doe", "123 Main St", "john@example.com"),
            List.of(new OrderItem("abc123", "Pizza", new BigDecimal("10.00"), 2)),
            new BigDecimal("20.00"),
            OrderStatus.CREATED,
            now,
            now);

    when(orderService.getById(id)).thenReturn(order);

    mvc.perform(get("/orders/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.customer.fullName").value("John Doe"))
        .andExpect(jsonPath("$.totalAmount").value(20.00))
        .andExpect(jsonPath("$.orderStatus").value("CREATED"));
  }

  @Test
  void getOrders_shouldReturn404_whenNotFound() throws Exception {
    String id = "missing";
    when(orderService.getById(id)).thenThrow(NotFoundException.order(id));

    mvc.perform(get("/orders/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Order with id " + id + " not found"));
  }
}
