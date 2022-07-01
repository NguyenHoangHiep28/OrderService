package com.example.orderservice.queue;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.enums.InventoryStatus;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.enums.PaymentStatus;
import com.example.orderservice.event.OrderEvent;
import com.example.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.orderservice.queue.Config.*;

@Service
@Log4j2
public class QueueService {

    private final AmqpTemplate rabbitTemplate;
    private final OrderRepository orderRepository;

    public QueueService(AmqpTemplate rabbitTemplate, OrderRepository orderRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderRepository = orderRepository;
    }

    public void handleInventory(OrderEvent orderEvent) {
        Optional<Order> orderOptional = orderRepository.findById(orderEvent.getOrderId());
        if (orderOptional.isPresent()) {
            String description = orderEvent.getMessage();
            Order order = orderOptional.get();
            order.setInventoryStatus(orderEvent.getInventoryStatus());
            // FAILED | PROCESSED | RETURN
//            if (orderEvent.getInventoryStatus().equals(InventoryStatus.FAILED)
//                    && order.getPaymentStatus().equals(PaymentStatus.PAID)) {
//                // Require refund in paymentService
//                orderEvent.setPaymentStatus(PaymentStatus.REFUND);
//                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, orderEvent);
//            }
            if (description != null) {
                if (order.getDescription() == null) {
                    order.setDescription(description);
                } else {
                    order.setDescription(order.getDescription() + "|" + description);
                }
            }
            orderRepository.save(order);
        }


    }

    public void handlePayment(OrderEvent orderEvent) {
        Optional<Order> orderOptional = orderRepository.findById(orderEvent.getOrderId());
        if (orderOptional.isPresent()) {
            String description = orderEvent.getMessage();
            Order order = orderOptional.get();
            order.setPaymentStatus(orderEvent.getPaymentStatus());
            // FAILED | PAID | REFUND
//            if (orderEvent.getPaymentStatus().equals(PaymentStatus.FAILED)
//                    && order.getInventoryStatus().equals(InventoryStatus.PROCESSED)){
//                orderEvent.setInventoryStatus(InventoryStatus.RETURN);
//                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, orderEvent);
//            }
            if (description != null) {
                if (order.getDescription() == null) {
                    order.setDescription(description);
                } else {
                    order.setDescription(order.getDescription() + "|" + description);
                }
            }
            orderRepository.save(order);
        }
    }
}
