package com.nhtr.orderservice.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateSingleOrderRequest extends CreateOrderBaseRequest {
    private String orderId;
    private String userId;
    private String accountId;
    private String symbol;
    private BigDecimal orderPrice;
    private Double orderQuantity;
}
