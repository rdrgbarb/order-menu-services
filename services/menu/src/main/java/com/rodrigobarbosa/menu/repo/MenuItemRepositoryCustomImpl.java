package com.rodrigobarbosa.menu.repo;

import com.rodrigobarbosa.menu.domain.MenuItem;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MenuItemRepositoryCustomImpl implements MenuItemRepositoryCustom {
  private final MongoTemplate mongoTemplate;

  public MenuItemRepositoryCustomImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public List<MenuItem> findWithOffsetLimit(long offset, int limit) {
    Query query = new Query().skip(offset).limit(limit);
    return mongoTemplate.find(query, MenuItem.class);
  }

  @Override
  public long totalRecords() {
    return mongoTemplate.count(new Query(), MenuItem.class);
  }
}
