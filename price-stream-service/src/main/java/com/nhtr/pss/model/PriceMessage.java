package com.nhtr.pss.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceMessage {
    private String symbol;
    private String price;
}
