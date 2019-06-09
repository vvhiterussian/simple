package com.vvhiterussian.websockets.simple.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Data
@Component
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketObj {
    private String id = "";
    private boolean auth = false;
    private int sessionLiveCountdown = 180;
}
