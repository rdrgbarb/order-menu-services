package com.rodrigobarbosa.order.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rodrigobarbosa.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerInvalidEnumTest {

  @Autowired MockMvc mockMvc;
  @MockitoBean OrderService service;

  @Test
  void patchStatus_invalidEnum_returns400() throws Exception {
    mockMvc
        .perform(
            patch("/orders/abc/status")
                .contentType("application/json")
                .content("{\"status\":\"NOT_A_REAL_STATUS\"}"))
        .andExpect(status().isBadRequest());
  }
}
