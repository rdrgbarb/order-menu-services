package com.rodrigobarbosa.order.service;

import com.rodrigobarbosa.order.api.dto.CreateOrderRequest;
import com.rodrigobarbosa.order.api.dto.OrderHistoryResponse;
import com.rodrigobarbosa.order.api.dto.OrderResponse;

public interface OrderService {
  OrderResponse create(CreateOrderRequest request);

  OrderResponse getById(String id);

  OrderHistoryResponse<OrderResponse> list(long offset, int limit);
}
