package com.nhtr.pss.model;

import lombok.Data;

@Data
public class SymbolLoadConfig {
    private String symbol;
    private double startPrice;
    private int messageCount;
    private int messagesPerSecond;
}
