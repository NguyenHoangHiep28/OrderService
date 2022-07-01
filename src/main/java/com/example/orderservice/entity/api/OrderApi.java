package com.example.orderservice.entity.api;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.dto.OrderRequestDTO;
import com.example.orderservice.event.OrderEvent;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.IOrderService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

import static com.example.orderservice.queue.Config.DIRECT_ROUTING_KEY_INVENTORY;
import static com.example.orderservice.queue.Config.TOPIC_EXCHANGE;
@CrossOrigin("*")
@RestController
@RequestMapping(path = "api/v1/orders")
public class OrderApi {
    private final AmqpTemplate rabbitTemplate;
    private final IOrderService orderService;

    public OrderApi(AmqpTemplate rabbitTemplate, IOrderService orderService) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderService = orderService;
    }
    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getOrders(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(orderService.getOrderByUserId(PageRequest.of(page, limit, Sort.by("createdAt").descending())));
    }
    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO orderRequestDTO) throws InterruptedException {
        if (orderRequestDTO != null && !orderRequestDTO.getOrderId().equals("")){
            OrderEvent result = orderService.placeOrder(orderRequestDTO.getOrderId());
            if (result != null) {
                rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, result);
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
