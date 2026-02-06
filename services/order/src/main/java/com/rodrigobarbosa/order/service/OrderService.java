package com.rodrigobarbosa.order.service;

import com.rodrigobarbosa.order.api.dto.CreateOrderRequest;

public interface OrderService {
  Object create(CreateOrderRequest request);
}
