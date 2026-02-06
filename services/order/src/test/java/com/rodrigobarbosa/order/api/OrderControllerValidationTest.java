package com.rodrigobarbosa.order.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rodrigobarbosa.order.api.error.GlobalExceptionHandler;
import com.rodrigobarbosa.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerValidationTest {

  @Autowired MockMvc mvc;

  @MockitoBean OrderService service;

  @Test
  void postOrders_shouldReturn400_whenMissingCustomer() throws Exception {
    mvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "orderItems": [
                            {
                                "productId": "abc123",
                                "quantity": 1
                            }
                        ]
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.details").isArray());
  }

  @Test
  void postOrders_shouldReturn400_whenEmptyOrderItems() throws Exception {
    mvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                      {
                        "customer": {"fullName":"John","address":"X","email":"john@example.com"},
                        "orderItems": []
                      }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.details").isArray());
  }

  @Test
  void postOrders_shouldReturn400_whenInvalidEmail() throws Exception {
    mvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                      {
                        "customer": {"fullName":"John","address":"X","email":"not-an-email"},
                        "orderItems": [{"productId":"abc123","quantity":1}]
                      }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.details").isArray());
  }

  @Test
  void postOrders_shouldReturn400_whenQuantityLessThanOne() throws Exception {
    mvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                      {
                        "customer": {"fullName":"John","address":"X","email":"john@example.com"},
                        "orderItems": [{"productId":"abc123","quantity":0}]
                      }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.details").isArray());
  }
}
