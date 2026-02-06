package com.rodrigobarbosa.order.api;

import com.rodrigobarbosa.order.api.dto.CreateOrderRequest;
import com.rodrigobarbosa.order.api.dto.OrderHistoryResponse;
import com.rodrigobarbosa.order.api.dto.OrderResponse;
import com.rodrigobarbosa.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @PostMapping
  public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
    return this.service.create(request);
  }

  @GetMapping("/{id}")
  public OrderResponse getById(@PathVariable String id) {
    return this.service.getById(id);
  }

  @GetMapping
  public OrderHistoryResponse<OrderResponse> list(
      @RequestParam(defaultValue = "0") @Min(0) long offset,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
    return this.service.list(offset, limit);
  }
}
