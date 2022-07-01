package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderDetail;
import com.example.orderservice.entity.enums.InventoryStatus;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.enums.PaymentStatus;
import com.example.orderservice.event.OrderDetailDTO;
import com.example.orderservice.event.OrderEvent;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderServiceImpl implements IOrderService{
    private final OrderRepository orderRepository;
    private final IAuthenticationFacade authenticationFacade;
    public OrderServiceImpl(OrderRepository orderRepository, IAuthenticationFacade authenticationFacade) {
        this.orderRepository = orderRepository;
        this.authenticationFacade = authenticationFacade;
    }


    @Override
    public Page<Order> getOrderByUserId(Pageable pageable) {
        String userId = authenticationFacade.getAuthenticatedUserId();
        List<Order> orderList = orderRepository.findUserOrders(userId, false);
        return new PageImpl<>(orderList);
    }

    @Override
    public OrderEvent placeOrder(String orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            Set<OrderDetailDTO> orderDetailDTOSet = new HashSet<>();
            OrderDetailDTO dto = null;
            for (OrderDetail detail:
                 order.getOrderDetails()) {
                dto = new OrderDetailDTO();
                dto.setOrderId(order.getId());
                dto.setProductId(detail.getId().getProductId());
                dto.setUnitPrice(detail.getUnitPrice());
                dto.setQuantity(detail.getQuantity());
                orderDetailDTOSet.add(dto);
            }
            OrderEvent orderEvent = OrderEvent.builder()
                    .orderId(order.getId())
                    .inventoryStatus(InventoryStatus.WAITING_FOR_PROCESSING)
                    .paymentStatus(PaymentStatus.WAITING_FOR_PROCESS)
                    .orderStatus(OrderStatus.PROCESSING)
                    .message(null)
                    .totalPrice(order.getTotalPrice())
                    .userId(order.getUserId())
                    .orderDetailDTOSet(orderDetailDTOSet)
                    .build();
            order.setPaymentStatus(PaymentStatus.WAITING_FOR_PROCESS);
            order.setInventoryStatus(InventoryStatus.WAITING_FOR_PROCESSING);
            order.setStatus(OrderStatus.PROCESSING);
            order.setIsShoppingCart(false);
            orderRepository.save(order);

            return orderEvent;
        }
        return null;
    }
}
