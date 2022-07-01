package com.example.orderservice.event;

import com.example.orderservice.entity.enums.InventoryStatus;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderEvent {
    private String orderId;
    private String userId;
    private Set<OrderDetailDTO> orderDetailDTOSet = new HashSet<>();
    private BigDecimal totalPrice;
    private PaymentStatus paymentStatus;
    private InventoryStatus inventoryStatus;
    private OrderStatus orderStatus;
//    private String device_token;
    private String message;
//    private String queueName;

    public boolean validationInventory(){
        return this.orderDetailDTOSet.size() > 0 && this.orderId != null && this.orderStatus != null && this.inventoryStatus != null && this.paymentStatus != null;
    }
}
