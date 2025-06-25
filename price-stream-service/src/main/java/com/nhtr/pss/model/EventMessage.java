package com.nhtr.pss.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventMessage {
    private String symbol;
    private String price;
    private long timestamp;
}
