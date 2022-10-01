package com.example.userservice.vo;

import java.util.Date;

import lombok.Data;

@Data
public class ResponseOrder {

    private String orderId; // 주문 ID
    private  String productId;
    private Integer qty; // 주문 수량
    private Integer unitPrice; // 단위 가격
    private Integer totalPrice;
    private Date createdAt;
}
