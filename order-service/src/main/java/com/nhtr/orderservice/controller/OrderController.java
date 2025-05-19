package com.nhtr.orderservice.controller;

import com.nhtr.orderservice.dto.request.CreateSingleOrderRequest;
import com.nhtr.orderservice.kafka.producer.OrderEventProducer;
import com.nhtr.orderservice.mapper.OrderEventMapper;
import com.nhtr.schema.OrderEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class OrderController {
    private final OrderEventProducer orderEventProducer;

    public OrderController(OrderEventProducer orderEventProducer) {
        this.orderEventProducer = orderEventProducer;
    }

    @PostMapping("/order/single")
    public ResponseEntity<?> createOrder(@RequestBody CreateSingleOrderRequest request) {
        OrderEvent orderEvent = OrderEventMapper.fromOrderRequest(request);
        orderEvent.setOrderId(UUID.randomUUID().toString());
        orderEventProducer.sendMessage(orderEvent);
        return ResponseEntity.ok("create order success");
    }

}
