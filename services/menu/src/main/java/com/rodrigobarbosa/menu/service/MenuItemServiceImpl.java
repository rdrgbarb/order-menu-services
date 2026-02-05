package com.rodrigobarbosa.menu.service;

import com.rodrigobarbosa.menu.api.dto.MenuItemCreateRequest;
import com.rodrigobarbosa.menu.api.dto.MenuItemResponse;
import com.rodrigobarbosa.menu.api.dto.MenuItemUpdateRequest;
import com.rodrigobarbosa.menu.api.dto.PaginatedResponse;
import com.rodrigobarbosa.menu.api.error.NotFoundException;
import com.rodrigobarbosa.menu.domain.MenuItem;
import com.rodrigobarbosa.menu.repo.MenuItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MenuItemServiceImpl implements MenuItemService {

  private final MenuItemRepository repo;

  public MenuItemServiceImpl(MenuItemRepository repo) {
    this.repo = repo;
  }

  @Override
  public MenuItemResponse create(MenuItemCreateRequest request) {
    MenuItem saved =
        repo.save(new MenuItem(null, request.name(), request.price(), request.available()));
    return toResponse(saved);
  }

  @Override
  public MenuItemResponse getById(String id) {
    return repo.findById(id)
        .map(this::toResponse)
        .orElseThrow(() -> NotFoundException.menuItem(id));
  }

  @Override
  public PaginatedResponse<MenuItemResponse> list(long offset, int limit) {
    List<MenuItemResponse> items =
        repo.findWithOffsetLimit(offset, limit).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    long total = repo.totalRecords();
    return new PaginatedResponse<>(total, items);
  }

  @Override
  public MenuItemResponse update(String id, MenuItemUpdateRequest request) {
    MenuItem item = repo.findById(id).orElseThrow(() -> NotFoundException.menuItem(id));
    if (request.name() != null) item.setName(request.name());
    if (request.price() != null) item.setPrice(request.price());
    if (request.available() != null) item.setAvailable(request.available());
    return toResponse(repo.save(item));
  }

  @Override
  public void delete(String id) {
    if (!repo.existsById(id)) throw NotFoundException.menuItem(id);
    repo.deleteById(id);
  }

  private MenuItemResponse toResponse(MenuItem item) {
    return new MenuItemResponse(item.getId(), item.getName(), item.getPrice(), item.isAvailable());
  }
}
