package com.rodrigobarbosa.order.api;

import com.rodrigobarbosa.order.api.dto.CreateOrderRequest;
import com.rodrigobarbosa.order.domain.Order;
import com.rodrigobarbosa.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @PostMapping
  public Order create(@Valid @RequestBody CreateOrderRequest request) {
    return service.create(request);
  }

  @GetMapping("/{id}")
  public Order getById(@PathVariable String id) {
    return this.service.getById(id);
  }
}
