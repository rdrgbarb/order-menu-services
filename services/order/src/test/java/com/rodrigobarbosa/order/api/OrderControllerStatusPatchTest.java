package com.rodrigobarbosa.order.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rodrigobarbosa.order.api.dto.UpdateOrderStatusRequest;
import com.rodrigobarbosa.order.api.error.GlobalExceptionHandler;
import com.rodrigobarbosa.order.api.error.NotFoundException;
import com.rodrigobarbosa.order.domain.OrderStatus;
import com.rodrigobarbosa.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerStatusPatchTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private OrderService orderService;

  @Test
  void patchStatus_shouldReturn404_whenOrderMissing() throws Exception {
    var request = new UpdateOrderStatusRequest(OrderStatus.CANCELLED);
    when(orderService.updateStatus("missing", request))
        .thenThrow(NotFoundException.order("missing"));

    mvc.perform(
            patch("/orders/missing/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CANCELLED\"}"))
        .andExpect(status().isNotFound());
  }
}
