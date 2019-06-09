package com.vvhiterussian.websockets.simple.controller;

import com.vvhiterussian.websockets.simple.model.AuthRequest;
import com.vvhiterussian.websockets.simple.model.AuthResponse;
import com.vvhiterussian.websockets.simple.model.WebSocketObj;
import com.vvhiterussian.websockets.simple.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.session.web.socket.events.SessionConnectEvent;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketObj webSocketObj;
    private final AuthService authService;

    private String sessionId;

    @MessageMapping("/auth")
    @SendTo("/auth")
    public AuthResponse auth(AuthRequest request, SimpMessageHeaderAccessor headerAccessor) {
        boolean isAuthenticated = authService.auth(request.getToken());
        webSocketObj.setAuth(isAuthenticated);
        webSocketObj.setId(sessionId);
        return new AuthResponse(request.getName() + (isAuthenticated ? ", you are authenticated!" : ", password is incorrect"));
    }

    @EventListener(SessionConnectEvent.class)
    public void connectHandler(SessionConnectEvent event) {
        String sessionId = event.getWebSocketSession().getId();
        log.debug("Session ID: {}", sessionId);

        this.sessionId = sessionId;
    }
}
