package com.rodrigobarbosa.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateOrderRequest(
    @NotNull @Valid CustomerRequest customer,
    @NotEmpty @Valid List<CreateOrderItemRequest> orderItems) {
  public record CustomerRequest(
      @NotBlank String fullName, @NotBlank String address, @NotBlank @Email String email) {}

  public record CreateOrderItemRequest(@NotBlank String productId, @Min(1) int quantity) {}
}
