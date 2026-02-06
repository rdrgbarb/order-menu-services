package com.rodrigobarbosa.order.api;

import com.rodrigobarbosa.order.api.dto.CreateOrderRequest;
import com.rodrigobarbosa.order.api.dto.OrderHistoryResponse;
import com.rodrigobarbosa.order.api.dto.OrderResponse;
import com.rodrigobarbosa.order.api.dto.UpdateOrderStatusRequest;
import com.rodrigobarbosa.order.api.error.ApiError;
import com.rodrigobarbosa.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order operations.")
public class OrderController {

  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @PostMapping
  @Operation(summary = "Create order")
  @ApiResponse(responseCode = "200", description = "Order created")
  @ApiResponse(
      responseCode = "400",
      description = "Validation failed / invalid productId",
      content =
          @Content(
              schema = @Schema(implementation = ApiError.class),
              examples =
                  @ExampleObject(
                      name = "Validation failed",
                      value =
                          """
            {
              "timestamp": "2026-02-06T10:20:30.123-03:00",
              "status": 400,
              "error": "Bad Request",
              "message": "Validation failed",
              "path": "/orders",
              "details": [
                { "field": "customer.email", "message": "must be a well-formed email address" }
              ]
            }
            """)))
  @ApiResponse(
      responseCode = "503",
      description = "Menu service unavailable",
      content =
          @Content(
              schema = @Schema(implementation = ApiError.class),
              examples =
                  @ExampleObject(
                      name = "Menu unavailable",
                      value =
                          """
            {
              "timestamp": "2026-02-06T10:20:30.123-03:00",
              "status": 503,
              "error": "Service Unavailable",
              "message": "Menu service unavailable",
              "path": "/orders",
              "details": null
            }
            """)))
  @ApiResponse(
      responseCode = "500",
      description = "Unexpected error",
      content =
          @Content(
              schema = @Schema(implementation = ApiError.class),
              examples =
                  @ExampleObject(
                      name = "Unexpected error",
                      value =
                          """
            {
              "timestamp": "2026-02-06T10:20:30.123-03:00",
              "status": 500,
              "error": "Internal Server Error",
              "message": "Unexpected error",
              "path": "/orders",
              "details": null
            }
            """)))
  public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
    return this.service.create(request);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get order by id")
  @ApiResponse(responseCode = "200", description = "Order found")
  @ApiResponse(
      responseCode = "404",
      description = "Order not found",
      content =
          @Content(
              schema = @Schema(implementation = ApiError.class),
              examples =
                  @ExampleObject(
                      name = "Order not found",
                      value =
                          """
          {
            "timestamp": "2026-02-06T10:20:30.123-03:00",
            "status": 404,
            "error": "Not Found",
            "message": "Order with id order_123 not found",
            "path": "/orders/order_123",
            "details": null
          }
          """)))
  @ApiResponse(
      responseCode = "500",
      description = "Unexpected error",
      content = @Content(schema = @Schema(implementation = ApiError.class)))
  public OrderResponse getById(@PathVariable String id) {
    return this.service.getById(id);
  }

  @GetMapping
  @Operation(summary = "List orders (paginated)")
  @ApiResponse(responseCode = "200", description = "Orders returned")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid pagination parameters",
      content = @Content(schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "500",
      description = "Unexpected error",
      content = @Content(schema = @Schema(implementation = ApiError.class)))
  public OrderHistoryResponse<OrderResponse> list(
      @RequestParam(defaultValue = "0") @Min(0) long offset,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
    return this.service.list(offset, limit);
  }

  @PatchMapping("/{id}/status")
  @Operation(summary = "Update order status")
  @ApiResponse(responseCode = "200", description = "Status updated")
  @ApiResponse(
      responseCode = "400",
      description = "Validation failed",
      content = @Content(schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Order not found",
      content =
          @Content(
              schema = @Schema(implementation = ApiError.class),
              examples =
                  @ExampleObject(
                      name = "Order not found",
                      value =
                          """
          {
            "timestamp": "2026-02-06T10:20:30.123-03:00",
            "status": 404,
            "error": "Not Found",
            "message": "Order with id order_123 not found",
            "path": "/orders/order_123/status",
            "details": null
          }
          """)))
  @ApiResponse(
      responseCode = "409",
      description = "Conflict",
      content =
          @Content(
              schema = @Schema(implementation = ApiError.class),
              examples =
                  @ExampleObject(
                      name = "Conflict",
                      value =
                          """
          {
            "timestamp": "2026-02-06T10:20:30.123-03:00",
            "status": 409,
            "error": "Conflict",
            "message": "Invalid status transition: DELIVERED -> PREPARING",
            "path": "/orders/order_123/status",
            "details": null
          }
          """)))
  @ApiResponse(
      responseCode = "500",
      description = "Unexpected error",
      content = @Content(schema = @Schema(implementation = ApiError.class)))
  public OrderResponse updateStatus(
      @PathVariable String id, @Valid @RequestBody UpdateOrderStatusRequest request) {
    return this.service.updateStatus(id, request);
  }
}
