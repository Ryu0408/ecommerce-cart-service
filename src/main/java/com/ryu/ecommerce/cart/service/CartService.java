package com.ryu.ecommerce.cart.service;

import com.ryu.ecommerce.cart.dto.CartItemView;
import java.util.List;

public interface CartService {
    void addItem(String cartKey, Long productId, int quantity); // set or increment 정책은 호출 측에서 결정
    void setQuantity(String cartKey, Long productId, int quantity);
    void removeItem(String cartKey, Long productId);
    void clear(String cartKey);
    List<CartItemView> getItems(String cartKey);
}
