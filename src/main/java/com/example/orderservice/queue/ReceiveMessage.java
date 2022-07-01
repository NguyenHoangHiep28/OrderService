package com.example.orderservice.queue;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.api.CartApi;
import com.example.orderservice.entity.api.EventApi;
import com.example.orderservice.entity.enums.InventoryStatus;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.enums.PaymentStatus;
import com.example.orderservice.event.OrderEvent;
import com.example.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.example.orderservice.queue.Config.*;

@Component
@Log4j2
public class ReceiveMessage {
    private final QueueService queueService;
    private final OrderRepository orderRepository;
    private final AmqpTemplate rabbitTemplate;

    public ReceiveMessage(QueueService queueService, OrderRepository orderRepository, AmqpTemplate rabbitTemplate) {
        this.queueService = queueService;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {QUEUE_ORDER})
    public void handleMessages(OrderEvent orderEvent) throws InterruptedException {
        if (!orderEvent.getInventoryStatus().equals(InventoryStatus.WAITING_FOR_PROCESSING)) {
            queueService.handleInventory(orderEvent);
        }

        if (!orderEvent.getPaymentStatus().equals(PaymentStatus.WAITING_FOR_PROCESS)){
            queueService.handlePayment(orderEvent);
        }
        Optional<Order> orderOptional = orderRepository.findById(orderEvent.getOrderId());
        if (orderOptional.isPresent()) {
            Order existingOrder = orderOptional.get();

            // Handle cases
            if (existingOrder.getInventoryStatus().equals(InventoryStatus.FAILED)
                    && existingOrder.getPaymentStatus().equals(PaymentStatus.PAID)) {
                existingOrder.setStatus(OrderStatus.REJECTED);
                orderEvent.setPaymentStatus(PaymentStatus.REFUND);
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, orderEvent);
                log.info("Send notification that order has been rejected due to the lack of product(s).");
                Thread.sleep(10000);
                EventApi.dis(existingOrder.getUserId().toString(), "Order has been processed failed! With reasons :" + existingOrder.getDescription());
            }

            if (existingOrder.getPaymentStatus().equals(PaymentStatus.FAILED)
                    && existingOrder.getInventoryStatus().equals(InventoryStatus.PROCESSED)) {
                existingOrder.setStatus(OrderStatus.CANCELED);
                orderEvent.setInventoryStatus(InventoryStatus.RETURN);
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, orderEvent);
            }

            if (existingOrder.getInventoryStatus().equals(InventoryStatus.FAILED)
                    && existingOrder.getPaymentStatus().equals(PaymentStatus.FAILED)) {
                existingOrder.setStatus(OrderStatus.CANCELED);
                log.info("Send notification that Order has been processed failed! With reasons : " + existingOrder.getDescription());
//                Thread.sleep(10000);
                EventApi.dis(existingOrder.getUserId().toString(), "Order has been processed failed! With reasons : " + existingOrder.getDescription());
                // close connection
//                EventApi.dis(existingOrder.getUserId().toString(), "close_connection");
            }
            if (existingOrder.getPaymentStatus().equals(PaymentStatus.REFUND_SUCCESS)) {
                log.info("Send notification that order has been refund successfully!");
                log.info(existingOrder.getId());
                EventApi.dis(existingOrder.getUserId().toString(), "Order has been refund successfully!");
                // close connection
//                EventApi.dis(existingOrder.getUserId().toString(), "close_connection");
            }
            if (existingOrder.getInventoryStatus().equals(InventoryStatus.RETURN_SUCCESS)) {
                log.info("Send notification that product has been returned successfully!");
            }
            if (existingOrder.getInventoryStatus().equals(InventoryStatus.PROCESSED)
                    && existingOrder.getPaymentStatus().equals(PaymentStatus.PAID)) {
                existingOrder.setStatus(OrderStatus.PROCESSED);
                log.info("Send notification that order has been processed successfully!");
                EventApi.dis(existingOrder.getUserId().toString(), "Order has been processed successfully!");
                // close connection
//                EventApi.dis(existingOrder.getUserId().toString(), "close_connection");
            }
            orderRepository.save(existingOrder);
        }
    }

}
