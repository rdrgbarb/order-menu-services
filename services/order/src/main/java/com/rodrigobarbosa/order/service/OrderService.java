package com.rodrigobarbosa.order.service;

import com.rodrigobarbosa.order.api.dto.CreateOrderRequest;
import com.rodrigobarbosa.order.domain.Order;

public interface OrderService {
  Order create(CreateOrderRequest request);

  Order getById(String id);
}
