package com.rodrigobarbosa.order.api.dto;

import com.rodrigobarbosa.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
