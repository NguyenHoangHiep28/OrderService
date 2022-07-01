package com.example.orderservice.event;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDetailDTO {
    private Integer productId;
    private String orderId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
}
