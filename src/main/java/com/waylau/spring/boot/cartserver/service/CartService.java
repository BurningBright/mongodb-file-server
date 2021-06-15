package com.waylau.spring.boot.cartserver.service;

import com.mongodb.client.result.UpdateResult;
import com.waylau.spring.boot.cartserver.domain.Cart;

import java.util.List;
import java.util.Optional;

public interface CartService {

    Cart saveCart(Cart cart);

    UpdateResult addCartItem(String id, Cart.Item item);

    void removeCart(String id);

    UpdateResult removeCartItem(String id, Long itemId);

    Optional<Cart> getCartById(String id);

    Optional<Cart> getCartByUser(Long userId);

    List<Cart> listCartByPage(int pageIndex, int pageSize);

}
