package com.rodrigobarbosa.menu.repo;

import com.rodrigobarbosa.menu.domain.MenuItem;
import java.util.List;

public interface MenuItemRepositoryCustom {
  List<MenuItem> findWithOffsetLimit(long offset, int limit);

  long totalRecords();
}
