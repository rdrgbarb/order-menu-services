package com.rodrigobarbosa.menu.service;

import com.rodrigobarbosa.menu.api.dto.MenuItemCreateRequest;
import com.rodrigobarbosa.menu.api.dto.MenuItemResponse;
import com.rodrigobarbosa.menu.api.dto.MenuItemUpdateRequest;
import com.rodrigobarbosa.menu.api.dto.PaginatedResponse;

public interface MenuItemService {
  MenuItemResponse create(MenuItemCreateRequest request);

  MenuItemResponse getById(String id);

  PaginatedResponse<MenuItemResponse> list(long offset, int limit);

  MenuItemResponse update(String id, MenuItemUpdateRequest request);

  void delete(String id);
}
