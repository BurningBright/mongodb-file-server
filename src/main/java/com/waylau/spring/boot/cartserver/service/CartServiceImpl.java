package com.waylau.spring.boot.cartserver.service;

import com.mongodb.client.result.UpdateResult;
import com.waylau.spring.boot.cartserver.domain.Cart;
import com.waylau.spring.boot.cartserver.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author chenguang.lin
 * @date 2021-06-11
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    public CartRepository cartRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public UpdateResult addCartItem(String id, Cart.Item item) {
        Update update = new Update();
        update.set("lastActivity", new Date());
        update.push("items", item);
        return mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(id)), update,"cart");
    }

    @Override
    public void removeCart(String id) {
        cartRepository.deleteById(id);
    }

    @Override
    public UpdateResult removeCartItem(String id, Long itemId) {
        Update update = new Update();
        update.set("lastActivity", new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        update.pull("items", map);
        return mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(id)), update,"cart");
    }

    @Override
    public Optional<Cart> getCartById(String id) {
        return cartRepository.findById(id);
    }

    @Override
    public Optional<Cart> getCartByUser(Long userId) {
        return Optional.ofNullable(mongoTemplate
                .findOne(new Query(Criteria.where("userId").is(userId)), Cart.class, "cart"));
    }

    @Override
    public List<Cart> listCartByPage(int pageIndex, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC,"uploadDate");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        return cartRepository.findAll(pageable).getContent();
    }

}
