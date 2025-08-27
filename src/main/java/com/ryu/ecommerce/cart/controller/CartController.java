package com.ryu.ecommerce.cart.controller;

import com.ryu.ecommerce.cart.dto.*;
import com.ryu.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) { this.cartService = cartService; }

    // 간단한 키 규칙: X-User-Id 또는 X-Session-Id 중 하나 필수
    private String key(Long userId, String sessionId) {
        if (userId != null) return "cart:user:" + userId;
        if (sessionId != null && !sessionId.isBlank()) return "cart:session:" + sessionId;
        throw new IllegalArgumentException("X-User-Id or X-Session-Id required");
    }

    @PostMapping("/items")
    public ResponseEntity<Void> add(@RequestHeader(value="X-User-Id", required=false) Long userId,
                                    @RequestHeader(value="X-Session-Id", required=false) String sessionId,
                                    @Valid @RequestBody AddItemRequest req) {
        cartService.addItem(key(userId, sessionId), req.productId(), req.quantity());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Void> setQuantity(
            @RequestHeader(value="X-User-Id", required=false) Long userId,
            @RequestHeader(value="X-Session-Id", required=false) String sessionId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateQuantityRequest req) {
        cartService.setQuantity(key(userId, sessionId), productId, req.quantity());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> remove(
            @RequestHeader(value="X-User-Id", required=false) Long userId,
            @RequestHeader(value="X-Session-Id", required=false) String sessionId,
            @PathVariable String productId) {
        cartService.removeItem(key(userId, sessionId), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@RequestHeader(value="X-User-Id", required=false) Long userId,
                                      @RequestHeader(value="X-Session-Id", required=false) String sessionId) {
        cartService.clear(key(userId, sessionId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public CartView get(@RequestHeader(value="X-User-Id", required=false) Long userId,
                        @RequestHeader(value="X-Session-Id", required=false) String sessionId) {
        var items = cartService.getItems(key(userId, sessionId));
        return CartView.of(items);
    }
}
