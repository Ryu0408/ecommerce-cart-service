package com.ryu.ecommerce.cart.service;

import com.ryu.ecommerce.cart.dto.CartItemView;
import java.util.List;

public interface CartService {
    void addItem(String cartKey, String productId, int quantity);
    void setQuantity(String cartKey, String productId, int quantity);
    void removeItem(String cartKey, String productId);
    void clear(String cartKey);
    List<CartItemView> getItems(String cartKey);
}

