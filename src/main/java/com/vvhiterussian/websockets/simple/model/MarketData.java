package com.vvhiterussian.websockets.simple.model;

import lombok.Data;

@Data
public class MarketData {
    private String idSource;
    private String securityId;
    private String exDestination;
    private double totalValue;
    private double bid;
    private double ask;
}
