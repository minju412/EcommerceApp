package com.example.userservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.userservice.vo.ResponseOrder;

@FeignClient(name = "order-service") // 호출하려는 마이크로 서비스 이름
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders_ng") // prefix + uri
    List<ResponseOrder> getOrders(@PathVariable String userId);
}
