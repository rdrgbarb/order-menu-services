package com.rodrigobarbosa.menu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MenuItemCreateRequest(
    @Schema(example = "Cheeseburger") @NotBlank String name,
    @Schema(example = "29.90") @NotNull @DecimalMin(value = "0.01") BigDecimal price,
    @Schema(example = "true") @NotNull Boolean available) {}
