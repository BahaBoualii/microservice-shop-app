package com.example.orderservice.repository;

import com.example.orderservice.models.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
