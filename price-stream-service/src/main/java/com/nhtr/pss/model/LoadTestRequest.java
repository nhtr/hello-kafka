package com.nhtr.pss.model;

import lombok.Data;

import java.util.List;

@Data
public class LoadTestRequest {
    private String symbol;
    private double startPrice;
    private int messageCount;
    private int messagesPerSecond;

    private List<SymbolLoadConfig> symbols;
}
