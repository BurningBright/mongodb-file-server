package com.waylau.spring.boot.cartserver.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author chenguang.lin
 * @date 2021-06-11
 */
@Document
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Cart {

    @Id  // 主键
    private String id;
    private Long userId;
    private Date lastActivity;
    private String status;
    private List<Item> items;

    @Data
    public static class Item {
        private Long itemId;
        private String title;
        private BigDecimal price;
        private Integer quantity;
        private String imgUrl;
    }

    @Data
    public static class ItemAggregate {
        private Long id;
        private Integer amount;
    }

}
