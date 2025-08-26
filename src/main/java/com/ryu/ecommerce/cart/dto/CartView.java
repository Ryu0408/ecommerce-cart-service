package com.ryu.ecommerce.cart.dto;

import java.util.List;

public record CartView(List<CartItemView> items, int totalQuantity) {
    public static CartView of(List<CartItemView> items) {
        int total = items.stream().mapToInt(CartItemView::quantity).sum();
        return new CartView(items, total);
    }
}
