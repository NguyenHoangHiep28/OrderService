package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.event.OrderEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    OrderEvent placeOrder(String orderId);
    Page<Order> getOrderByUserId(Pageable pageable);
}
