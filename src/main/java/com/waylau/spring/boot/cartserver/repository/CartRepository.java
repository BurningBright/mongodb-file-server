package com.waylau.spring.boot.cartserver.repository;

import com.waylau.spring.boot.cartserver.domain.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
}
