package com.rodrigobarbosa.menu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record MenuItemUpdateRequest(
    @Schema(example = "Cheeseburger Deluxe") String name,
    @Schema(example = "31.90") @DecimalMin(value = "0.01") BigDecimal price,
    @Schema(example = "true") Boolean available) {}
