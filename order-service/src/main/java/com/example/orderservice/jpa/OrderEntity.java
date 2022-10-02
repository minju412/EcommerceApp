package com.example.orderservice.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId; // 상품 ID

    @Column(nullable = false)
    private Integer qty; // 수량

    @Column(nullable = false)
    private Integer unitPrice; // 단가

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private String userId; // 주문한 사용자 ID

    @Column(nullable = false)
    private String orderId; // 주문 ID

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date createdAt;
}
