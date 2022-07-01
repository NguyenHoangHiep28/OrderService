package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Order findOrderByUserIdAndIsShoppingCart(String userId, Boolean isShoppingCart);
    @Query("select o from Order o where o.userId = :userId and o.isShoppingCart = :isShoppingCart order by o.createdAt desc")
    List<Order> findUserOrders(@Param("userId") String userId,@Param("isShoppingCart") Boolean isShoppingCart);
}
