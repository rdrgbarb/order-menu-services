package com.rodrigobarbosa.order.repo;

import com.rodrigobarbosa.order.domain.Order;
import java.util.List;

public interface OrderRepositoryCustom {
  List<Order> findWithOffsetLimit(long offset, int size);

  long totalRecords();
}
