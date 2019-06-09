package com.vvhiterussian.websockets.simple.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecurityDefinitionService {
    public List<String> getSecurityDefinitions() {
        return Arrays.asList("RU000000001", "RU000000002", "RU000000003");
    }
}
