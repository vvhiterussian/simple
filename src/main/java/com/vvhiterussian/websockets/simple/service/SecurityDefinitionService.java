package com.vvhiterussian.websockets.simple.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SecurityDefinitionService implements ApplicationRunner {

    @Getter
    private List<String> securityDefinitions = new ArrayList<>();

    @Getter
    @Setter
    private boolean isShutdown = false;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        while (!isShutdown) {
            securityDefinitions = new ArrayList<>();
            for (int i = 0; i < new Random().nextInt(9) + 1; i++) {
                securityDefinitions.add("RU000000000" + new Random().nextInt(10));
            }
            Thread.sleep(3000);
        }
    }
}
