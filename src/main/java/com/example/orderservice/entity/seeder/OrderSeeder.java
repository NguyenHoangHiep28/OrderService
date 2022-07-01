package com.example.orderservice.entity.seeder;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderDetail;
import com.example.orderservice.entity.OrderDetailId;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.repository.OrderDetailRepository;
import com.example.orderservice.repository.OrderRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

//@Component
public class OrderSeeder implements CommandLineRunner {
//    private final Faker faker = new Faker();
//    private final OrderRepository orderRepository;
//    private final OrderDetailRepository orderDetailRepository;
//
//    public OrderSeeder(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
//        this.orderRepository = orderRepository;
//        this.orderDetailRepository = orderDetailRepository;
//    }
    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Override
    public void run(String... args) throws Exception {
    subcribe();
    dis();
    }
    private SseEmitter subcribe() {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
        emitters.add(sseEmitter);
        return sseEmitter;
    }

    private void dis() {
        for (SseEmitter emitter:
             emitters) {
            try {
                emitter.send(SseEmitter.event().name("test"));
            } catch (IOException exception) {
                emitters.remove(emitter);
            }
        }
    }
//    private void createOrders() {
//        List<Order> orders = new ArrayList<>();
//        List<OrderDetail> orderDetails = new ArrayList<>();
//        boolean existProduct = false;
//
//        for (int i = 1; i <= 1; i++) {
//            Order order = new Order();
//            long total = 0;
//            int userId = 1;
//            int orderDetailNumber = faker.number().numberBetween(1, 3);
//            String orderId = UUID.randomUUID().toString();
//            System.out.println(order.getId());
//            order.setId(orderId);
//            order.setStatus(OrderStatus.PENDING);
//            order.setUserId(userId);
//            for (int j = 0; j < orderDetailNumber; j++) {
//                int productId = faker.number().numberBetween(1, 3);
//                for (OrderDetail od :
//                        orderDetails) {
//                    if (od.getId().getProductId() == productId && order.getUserId() == userId) {
//                        existProduct = true;
//                        break;
//                    }
//                }
//                if (existProduct) {
//                    j--;
//                    existProduct = false;
//                    continue;
//                }
//                OrderDetail orderDetail = new OrderDetail();
//                OrderDetailId orderDetailId = new OrderDetailId();
//                orderDetailId.setOrderId(orderId);
//                orderDetailId.setProductId(productId);
////                orderDetail.setProductId(productId);
//                int quantity = faker.number().numberBetween(1, 5);
////                orderDetail.setOrderId(orderId);
//                orderDetail.setQuantity(quantity);
//                long unitPrice = 0;
//                orderDetail.setUnitPrice(new BigDecimal(unitPrice));
//                total += unitPrice;
//                orderDetails.add(orderDetail);
//            }
//            order.setTotalPrice(new BigDecimal(total));
//            orders.add(order);
////            if (i % 2 == 0) {
//                orderRepository.saveAll(orders);
//                orderDetailRepository.saveAll(orderDetails);
//                orders.clear();
//                orderDetails.clear();
////            }
//        }
//    }
}
