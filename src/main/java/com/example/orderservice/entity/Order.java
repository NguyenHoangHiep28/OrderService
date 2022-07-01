package com.example.orderservice.entity;

import com.example.orderservice.entity.base.BaseEntity;
import com.example.orderservice.entity.enums.InventoryStatus;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "ord-generator",
//            parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "order"),
//            strategy = "com.example.ecommerce.config.MyGenerator")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
    // custom string id
    private String id;
    private String userId;
    private BigDecimal totalPrice;
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;
    @OneToMany(mappedBy = "order",
            fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<OrderDetail> orderDetails;
    private String description;
    private Boolean isShoppingCart;
    @Enumerated(EnumType.ORDINAL)
    private InventoryStatus inventoryStatus;
    @Enumerated(EnumType.ORDINAL)
    private PaymentStatus paymentStatus;
}

