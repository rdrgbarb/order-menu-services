package com.rodrigobarbosa.menu.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rodrigobarbosa.menu.api.dto.MenuItemResponse;
import com.rodrigobarbosa.menu.api.dto.PaginatedResponse;
import com.rodrigobarbosa.menu.api.error.NotFoundException;
import com.rodrigobarbosa.menu.service.MenuItemService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MenuItemController.class)
class MenuItemControllerTest {

  @Autowired MockMvc mvc;
  @MockitoBean MenuItemService service;

  @Test
  void post_menuItems_validation_and_created() throws Exception {
    when(service.create(any()))
        .thenReturn(new MenuItemResponse("id1", "Burger", new BigDecimal("10.00"), true));

    // invalid: missing_name
    mvc.perform(
            post("/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "price": 10.00,
                        "available": true
                    }
                    """))
        .andExpect(status().isBadRequest());

    // valid
    mvc.perform(
            post("/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                                "name": "Burger",
                                "price": 10.00,
                                "available": true
                            }
                        """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("id1"))
        .andExpect(jsonPath("$.name").value("Burger"));

    verify(service, times(1)).create(any());
  }

  @Test
  void get_menuItem_by_id_returns_200_or_404() throws Exception {
    when(service.getById("ok"))
        .thenReturn(new MenuItemResponse("ok", "Burger", new BigDecimal("10.00"), true));
    when(service.getById("missing")).thenThrow(NotFoundException.menuItem("missing"));

    mvc.perform(get("/menu-items/ok"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("ok"))
        .andExpect(jsonPath("$.name").value("Burger"));

    mvc.perform(get("/menu-items/missing")).andExpect(status().isNotFound());
  }

  @Test
  void get_menuItems_list_returns_items_and_totalRecords() throws Exception {
    when(service.list(0, 2))
        .thenReturn(
            new PaginatedResponse<>(
                10,
                List.of(
                    new MenuItemResponse("1", "A", new BigDecimal("1.00"), true),
                    new MenuItemResponse("2", "B", new BigDecimal("2.00"), false))));

    mvc.perform(get("/menu-items?offset=0&limit=2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalRecords").value(10))
        .andExpect(jsonPath("$.items.length()").value(2))
        .andExpect(jsonPath("$.items[0].id").value("1"))
        .andExpect(jsonPath("$.items[1].id").value("2"));
  }

  @Test
  void put_menuItem_updates_or_404() throws Exception {
    when(service.update(eq("ok"), any()))
        .thenReturn(new MenuItemResponse("ok", "New", new BigDecimal("12.00"), true));
    when(service.update(eq("missing"), any())).thenThrow(NotFoundException.menuItem("missing"));

    mvc.perform(
            put("/menu-items/ok")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "name": "New",
                            "price": 12.00,
                            "available": true
                        }
                        """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New"));

    mvc.perform(
            put("/menu-items/missing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "name": "New"
                        }
                        """))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_menuItem_deletes_or_404() throws Exception {
    doNothing().when(service).delete("ok");
    doThrow(NotFoundException.menuItem("missing")).when(service).delete("missing");

    mvc.perform(delete("/menu-items/ok")).andExpect(status().isNoContent());

    mvc.perform(delete("/menu-items/missing")).andExpect(status().isNotFound());
  }
}
