package com.vvhiterussian.websockets.simple.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public boolean auth(String token) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return true;
    }
}
