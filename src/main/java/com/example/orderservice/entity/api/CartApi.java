package com.example.orderservice.entity.api;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.dto.CartItemDTO;
import com.example.orderservice.entity.dto.ShoppingCart;
import com.example.orderservice.service.IShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@CrossOrigin("*")
@RestController
@RequestMapping(path = "api/v1/carts")
public class CartApi {
    private final IShoppingCartService shoppingCartService;

    public CartApi(IShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }
    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ShoppingCart> getShoppingCart() {
        ShoppingCart shoppingCart = shoppingCartService.getUserCart();
        if (shoppingCart != null) {
            return ResponseEntity.ok(shoppingCart);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.DELETE, path = "{productId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable(name = "productId") Integer productId) {
        boolean successfullyDeleted = shoppingCartService.deleteCartItem(productId);
        if (successfullyDeleted) {
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.badRequest().build();
        }
    }
    @RolesAllowed("user")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Order> addToCart(@RequestBody CartItemDTO cartItemDTO) {
        return ResponseEntity.ok(shoppingCartService.addItemToCart(cartItemDTO));
    }
}
