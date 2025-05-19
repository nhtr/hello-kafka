package com.nhtr.orderservice.dto.request;

import lombok.Data;

@Data
public class CreateOrderBaseRequest {
    private String orderId;
    private String userId;
    private String accountId;
}
