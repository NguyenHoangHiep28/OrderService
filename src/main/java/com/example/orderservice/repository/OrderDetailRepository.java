package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderDetail;
import com.example.orderservice.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}
