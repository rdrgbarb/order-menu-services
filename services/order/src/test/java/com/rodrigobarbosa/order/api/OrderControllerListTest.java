package com.rodrigobarbosa.order.api;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class OrderControllerListTest {
  @Autowired private MockMvc mockMvc;
  @MockitoBean OrderService orderService;

  @Test
  void list_shouldReturnOrders_withLimitOffsetTotalRecords() throws Exception {
    mockMvc.perform(get("/orders").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

    verify(orderService, times(1)).list(0L, 20);
  }

  @Test
  void list_shouldUseProvidedOffsetAndLimit() throws Exception {
    mockMvc
        .perform(
            get("/orders")
                .param("offset", "5")
                .param("limit", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(orderService, times(1)).list(5L, 10);
  }

  @Test
  void list_shouldReturnBadRequest_whenLimitExceedsMax() throws Exception {
    mockMvc
        .perform(get("/orders").param("limit", "101").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(orderService);
  }

  @Test
  void list_shouldReturnBadRequest_whenOffsetNegative() throws Exception {
    mockMvc
        .perform(get("/orders").param("offset", "-1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(orderService);
  }
}
