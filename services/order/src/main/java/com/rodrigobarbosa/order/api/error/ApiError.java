package com.rodrigobarbosa.order.api.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(name = "ApiError", description = "Standard error response payload.")
public record ApiError(
    @Schema(example = "2026-02-06T10:20:30.123-03:00") OffsetDateTime timestamp,
    @Schema(example = "400") int status,
    @Schema(example = "Bad Request") String error,
    @Schema(example = "Validation failed") String message,
    @Schema(example = "/orders") String path,
    List<ApiErrorDetail> details) {

  public record ApiErrorDetail(
      @Schema(example = "customer.email") String field,
      @Schema(example = "must be a well-formed email address") String message) {}
}
