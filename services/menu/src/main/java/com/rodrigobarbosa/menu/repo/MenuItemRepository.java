package com.rodrigobarbosa.menu.repo;

import com.rodrigobarbosa.menu.domain.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuItemRepository
    extends MongoRepository<MenuItem, String>, MenuItemRepositoryCustom {}
