package com.example.catalogservice.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CatalogDto implements Serializable { // 직렬화

    private String productId;
    private Integer qty; // 수량
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId; // 주문 ID
    private String userId; // 사용자 ID
}
