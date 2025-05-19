package com.nhtr.orderservice.mapper;

import com.nhtr.orderservice.dto.request.CreateSingleOrderRequest;
import com.nhtr.schema.OrderEvent;

public class OrderEventMapper {
    public static OrderEvent fromOrderRequest(CreateSingleOrderRequest request) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderId(request.getOrderId());
        orderEvent.setOrderPrice(request.getOrderPrice().doubleValue());
        return orderEvent;
    }
}
