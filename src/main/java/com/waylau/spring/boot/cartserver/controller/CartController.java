package com.waylau.spring.boot.cartserver.controller;

import com.mongodb.client.result.UpdateResult;
import com.waylau.spring.boot.cartserver.domain.Cart;
import com.waylau.spring.boot.cartserver.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author chenguang.lin
 * @date 2021-06-11
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private MongoTemplate mongoTemplate;

    // 加入购物车
    @PostMapping("/cart/item")
    public ResponseEntity<Object> handleCartAdd(@RequestBody Cart.Item item) {
        Optional<Cart> usrCart = cartService.getCartByUser(777L);
        if (!usrCart.isPresent()) {
            Cart cart = new Cart();
            cart.setUserId(777L);
            cart.setLastActivity(new Date());
            cart.setStatus("active");
            List<Cart.Item> list = new ArrayList<>();
            list.add(item);
            cart.setItems(list);
            cartService.saveCart(cart);
            return ResponseEntity.ok().body("done");
        }
        UpdateResult result = cartService.addCartItem(usrCart.get().getId(), item);
        if (result.wasAcknowledged())
            return ResponseEntity.ok().body("done");
        return ResponseEntity.ok().body("failed");
    }

    // 移除购物车物品
    @DeleteMapping("/cart/item/{itemId}")
    public ResponseEntity<Object> handleCartDelete(@PathVariable Long itemId) {
        Optional<Cart> cart = cartService.getCartByUser(777L);
        if (cart.isPresent()) {
            UpdateResult result = cartService.removeCartItem(cart.get().getId(), itemId);
            if (result.wasAcknowledged())
                return ResponseEntity.ok().body("done");
            return ResponseEntity.ok().body("failed");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cart was not fount");
        }
    }

    // 购物车详情
    @GetMapping("cart/{id}")
    @ResponseBody
    public ResponseEntity<Object> getCart(@PathVariable String id) {
        Optional<Cart> cart = cartService.getCartById(id);
        if (cart.isPresent()) {
            return ResponseEntity.ok().body(cart.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cart was not fount");
        }
    }

    // 删除购物车
    @DeleteMapping("cart/{id}")
    @ResponseBody
    public ResponseEntity<Object> deleteCart(@PathVariable String id) {
        Optional<Cart> cart = cartService.getCartById(id);
        if (cart.isPresent()) {
            cartService.removeCart(id);
            return ResponseEntity.ok().body("done");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cart was not fount");
        }
    }

    // 用户购物车详情
    @GetMapping("cart/user/{userId}")
    @ResponseBody
    public ResponseEntity<Object> getCartByUser(@PathVariable Long userId) {
        Optional<Cart> cart = cartService.getCartByUser(userId);
        if (cart.isPresent()) {
            return ResponseEntity.ok().body(cart.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cart was not fount");
        }
    }

    // 购物车分页
    @GetMapping("cart/{pageIndex}/{pageSize}")
    @ResponseBody
    public List<Cart> lisCartByPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return cartService.listCartByPage(pageIndex, pageSize);
    }

    // 购物车数据聚合
    @GetMapping("cart/aggregate")
    @ResponseBody
    public List<Cart.ItemAggregate> aggregateCart() {
        Aggregation agg = Aggregation.newAggregation(
                // 条件
                Aggregation.match(Criteria.where("userId").is(777L))
                ,
                // 展开
                Aggregation.unwind("items")
                ,
                // 分组
                Aggregation.group("items.itemId").sum("items.quantity").as("amount")
        );
        AggregationResults<Cart.ItemAggregate> outputType =
                mongoTemplate.aggregate(agg, "cart", Cart.ItemAggregate.class);
        return outputType.getMappedResults();
    }

}
