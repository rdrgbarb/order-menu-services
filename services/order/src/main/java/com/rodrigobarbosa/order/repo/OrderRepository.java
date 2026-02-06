package com.rodrigobarbosa.order.repo;

import com.rodrigobarbosa.order.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String>, OrderRepositoryCustom {}
