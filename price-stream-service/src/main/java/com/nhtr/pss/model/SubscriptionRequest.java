package com.nhtr.pss.model;

import lombok.Data;
import java.util.List;

@Data
public class SubscriptionRequest {
    private String userId;
    private List<String> symbols;
}
