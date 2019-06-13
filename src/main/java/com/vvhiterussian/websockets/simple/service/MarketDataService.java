package com.vvhiterussian.websockets.simple.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MarketDataService {

    private Map<String, Object> destinations = new HashMap<>();
    // держит grpc

    public void addSubscription(String destination) {
        // кидает в бидирекшинал стрим запрос на включение бумаги в стрим
    }
}
