package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.dto.CartItemDTO;
import com.example.orderservice.entity.dto.ShoppingCart;
import com.example.orderservice.event.OrderDetailDTO;

public interface IShoppingCartService {
    Order addItemToCart(CartItemDTO cartItemDTO);
    ShoppingCart getUserCart();
    boolean deleteCartItem(Integer productId);
}
