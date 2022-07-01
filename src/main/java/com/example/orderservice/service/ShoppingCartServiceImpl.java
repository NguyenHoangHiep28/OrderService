package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderDetail;
import com.example.orderservice.entity.OrderDetailId;
import com.example.orderservice.entity.dto.CartItemDTO;
import com.example.orderservice.entity.dto.ShoppingCart;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.repository.OrderDetailRepository;
import com.example.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Log4j2
public class ShoppingCartServiceImpl implements IShoppingCartService{
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final IAuthenticationFacade authenticationFacade;

    public ShoppingCartServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, IAuthenticationFacade authenticationFacade) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public boolean deleteCartItem(Integer productId) {
        String userId = authenticationFacade.getAuthenticatedUserId();
        Order existingCart = orderRepository.findOrderByUserIdAndIsShoppingCart(userId, true);
        if (existingCart != null) {
            Set<OrderDetail> cartItems = new LinkedHashSet<>();
            if (existingCart.getOrderDetails().size() > 0) {
                for (OrderDetail item: existingCart.getOrderDetails()) {
                    if (item.getId().getProductId().equals(productId)){
                        existingCart.setTotalPrice(existingCart.getTotalPrice().subtract(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
                        cartItems.add(item);
                    }
                }
                orderRepository.save(existingCart);
                orderDetailRepository.deleteAll(cartItems);
                return true;
            }
        }
        return false;
    }

    @Override
    public Order addItemToCart(CartItemDTO cartItemDTO) {
        String userId = authenticationFacade.getAuthenticatedUserId();
        Order existingCart = orderRepository.findOrderByUserIdAndIsShoppingCart(userId, true);
        if(existingCart != null) {
            Set<OrderDetail> cartItems;
            if (existingCart.getOrderDetails().size() > 0) {
                OrderDetail modifiedCartItem = null;
                for (OrderDetail cartItem:
                        existingCart.getOrderDetails()) {
                    if (cartItem.getId().getProductId().equals(cartItemDTO.getProductId())) {
                        modifiedCartItem = cartItem;
                        cartItem.setQuantity(cartItem.getQuantity() + 1);
                        break;
                    }
                }
                if (modifiedCartItem != null) {
                    BigDecimal newTotalPrice = existingCart.getTotalPrice()
                            .add(modifiedCartItem.getUnitPrice());
                    existingCart.setTotalPrice(newTotalPrice);
                    cartItems = existingCart.getOrderDetails();
                    orderRepository.save(existingCart);
                    orderDetailRepository.saveAll(cartItems);
                } else {
                    OrderDetail newCartItem = new OrderDetail(
                            new OrderDetailId(existingCart.getId(), cartItemDTO.getProductId()),
                            existingCart,
                            cartItemDTO.getProductName(),
                            cartItemDTO.getThumbnails(),
                            cartItemDTO.getQuantity(),
                            cartItemDTO.getUnitPrice());
                    cartItems = existingCart.getOrderDetails();
                    cartItems.add(newCartItem);
                    existingCart.setOrderDetails(cartItems);
                    BigDecimal newTotalPrice = existingCart.getTotalPrice().add(newCartItem.getUnitPrice().multiply(BigDecimal.valueOf(newCartItem.getQuantity())));
                    existingCart.setTotalPrice(newTotalPrice);
                    orderDetailRepository.saveAll(cartItems);
                    orderRepository.save(existingCart);
                }
            } else {
                cartItems = new LinkedHashSet<>();
                OrderDetail newCartItem = new OrderDetail(
                        new OrderDetailId(existingCart.getId(), cartItemDTO.getProductId()),
                        existingCart,
                        cartItemDTO.getProductName(),
                        cartItemDTO.getThumbnails(),
                        cartItemDTO.getQuantity(),
                        cartItemDTO.getUnitPrice());
                cartItems.add(newCartItem);
//                existingCart.setOrderDetails(cartItems);
                orderRepository.save(existingCart);
                orderDetailRepository.saveAll(cartItems);
            }
            return existingCart;
        } else {
            Order newShoppingCart = new Order();
            newShoppingCart.setId(UUID.randomUUID().toString());
            System.out.println(newShoppingCart.getId());
            Set<OrderDetail> cartItems = new LinkedHashSet<>();
            OrderDetail newCartItem = new OrderDetail(
                    new OrderDetailId(newShoppingCart.getId(), cartItemDTO.getProductId()),
                    newShoppingCart,
                    cartItemDTO.getProductName(),
                    cartItemDTO.getThumbnails(),
                    cartItemDTO.getQuantity(),
                    cartItemDTO.getUnitPrice());
            cartItems.add(newCartItem);
//            newShoppingCart.setOrderDetails(cartItems);
            newShoppingCart.setIsShoppingCart(true);
            newShoppingCart.setStatus(OrderStatus.PENDING);
            newShoppingCart.setTotalPrice(newCartItem.getUnitPrice().multiply(BigDecimal.valueOf(newCartItem.getQuantity())));
            newShoppingCart.setUserId(userId);
            orderRepository.save(newShoppingCart);
            orderDetailRepository.saveAll(cartItems);
            return newShoppingCart;
        }
    }

    @Override
    public ShoppingCart getUserCart() {
        String userId = authenticationFacade.getAuthenticatedUserId();
        Order userCart = orderRepository.findOrderByUserIdAndIsShoppingCart(userId, true);
        if (userCart != null) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setId(userCart.getId());
            shoppingCart.setTotalPrice(userCart.getTotalPrice());
            List<CartItemDTO> cartItemDTOS = new ArrayList<>();
            CartItemDTO cartItemDTO;
            for (OrderDetail detail:
                    userCart.getOrderDetails()) {
                cartItemDTO = new CartItemDTO();
//                cartItemDTO.setOrderId(detail.getOrder().getId());
                cartItemDTO.setQuantity(detail.getQuantity());
                cartItemDTO.setProductId(detail.getId().getProductId());
                cartItemDTO.setThumbnails(detail.getThumbnails());
                cartItemDTO.setUnitPrice(detail.getUnitPrice());
                cartItemDTO.setProductName(detail.getProductName());
                cartItemDTOS.add(cartItemDTO);
            }
            shoppingCart.setCartItemDTOS(cartItemDTOS);
            return shoppingCart;
        }
        return null;
    }
}
