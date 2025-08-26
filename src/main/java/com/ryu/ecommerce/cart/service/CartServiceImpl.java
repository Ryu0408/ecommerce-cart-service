package com.ryu.ecommerce.cart.service;

import com.ryu.ecommerce.cart.dto.CartItemView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final long ttlDays;

    public CartServiceImpl(RedisTemplate<String, Object> redisTemplate,
                           @Value("${app.cart.ttl-days:30}") long ttlDays) {
        this.redisTemplate = redisTemplate;
        this.ttlDays = ttlDays;
    }

    private HashOperations<String, String, Integer> ops() {
        return redisTemplate.opsForHash();
    }

    private void touchTTL(String key) {
        redisTemplate.expire(key, Duration.ofDays(ttlDays));
    }

    @Override
    public void addItem(String key, Long productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        var field = String.valueOf(productId);
        Integer cur = ops().get(key, field);
        int next = (cur == null ? 0 : cur) + quantity;
        ops().put(key, field, next);
        touchTTL(key);
    }

    @Override
    public void setQuantity(String key, Long productId, int quantity) {
        var field = String.valueOf(productId);
        if (quantity <= 0) {
            ops().delete(key, field);
        } else {
            ops().put(key, field, quantity);
            touchTTL(key);
        }
    }

    @Override
    public void removeItem(String key, Long productId) {
        ops().delete(key, String.valueOf(productId));
        touchTTL(key);
    }

    @Override
    public void clear(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public List<CartItemView> getItems(String key) {
        Map<String, Integer> map = ops().entries(key);
        return map.entrySet().stream()
                .map(e -> new CartItemView(Long.valueOf(e.getKey()), e.getValue()))
                .toList();
    }
}
