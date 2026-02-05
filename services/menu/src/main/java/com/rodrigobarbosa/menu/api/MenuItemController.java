package com.rodrigobarbosa.menu.api;

import com.rodrigobarbosa.menu.api.dto.MenuItemCreateRequest;
import com.rodrigobarbosa.menu.api.dto.MenuItemResponse;
import com.rodrigobarbosa.menu.api.dto.MenuItemUpdateRequest;
import com.rodrigobarbosa.menu.api.dto.PaginatedResponse;
import com.rodrigobarbosa.menu.service.MenuItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu-items")
@Tag(name = "Menu")
public class MenuItemController {

  private final MenuItemService service;

  public MenuItemController(MenuItemService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MenuItemResponse create(@Valid @RequestBody MenuItemCreateRequest request) {
    return service.create(request);
  }

  @GetMapping("/{id}")
  public MenuItemResponse getById(@PathVariable String id) {
    return service.getById(id);
  }

  @GetMapping
  public PaginatedResponse<MenuItemResponse> list(
      @RequestParam(defaultValue = "0") @Min(0) long offset,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
    return service.list(offset, limit);
  }

  @PutMapping("/{id}")
  public MenuItemResponse update(
      @PathVariable String id, @Valid @RequestBody MenuItemUpdateRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    service.delete(id);
  }
}
