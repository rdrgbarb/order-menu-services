package com.rodrigobarbosa.order.repo;

import com.rodrigobarbosa.order.domain.Order;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

  private final MongoTemplate mongoTemplate;

  public OrderRepositoryCustomImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public List<Order> findWithOffsetLimit(long offset, int limit) {
    Query query = new Query().skip(offset).limit(limit);
    return mongoTemplate.find(query, Order.class);
  }

  @Override
  public long totalRecords() {
    return mongoTemplate.count(new Query(), Order.class);
  }
}
