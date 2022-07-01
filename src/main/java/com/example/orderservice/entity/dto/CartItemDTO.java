package com.example.orderservice.entity.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CartItemDTO {
    private Integer productId;
//    private String orderId;
    private String productName;
    private String thumbnails;
    private int quantity;
    private BigDecimal unitPrice;
}
