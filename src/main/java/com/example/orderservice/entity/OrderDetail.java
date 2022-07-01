package com.example.orderservice.entity;

import com.example.orderservice.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderDetail extends BaseEntity {
    @EmbeddedId
    private OrderDetailId id = new OrderDetailId();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Order order;
//    @ManyToOne
//    @MapsId("productId")
//    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
//    @JsonManagedReference
//    private Product product;
//    @Column(name = "product_id", updatable = false, insertable = false)
//    private Integer productId;
//    @Column(name = "order_id", updatable = false, insertable = false)
//    private String orderId;
    private String productName;
    private String thumbnails;
    private Integer quantity;
    private BigDecimal unitPrice;

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", thumbnails='" + thumbnails + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
