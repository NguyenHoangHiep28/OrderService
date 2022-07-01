package com.example.orderservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShoppingCart {
    private String id;
    private List<CartItemDTO> cartItemDTOS;
    private BigDecimal totalPrice;
}
