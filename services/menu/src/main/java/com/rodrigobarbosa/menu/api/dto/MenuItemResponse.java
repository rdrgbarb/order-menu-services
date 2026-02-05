package com.rodrigobarbosa.menu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record MenuItemResponse(
    @Schema(example = "65a7f...") String id,
    @Schema(example = "Cheeseburger") String name,
    @Schema(example = "29.90") BigDecimal price,
    @Schema(example = "true") boolean available) {}
