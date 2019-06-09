package com.vvhiterussian.websockets.simple.controller;

import com.vvhiterussian.websockets.simple.model.AuthRequest;
import com.vvhiterussian.websockets.simple.model.AuthResponse;
import com.vvhiterussian.websockets.simple.model.SecurityDefinitionResponse;
import com.vvhiterussian.websockets.simple.model.WebSocketObj;
import com.vvhiterussian.websockets.simple.service.AuthService;
import com.vvhiterussian.websockets.simple.service.SecurityDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.session.web.socket.events.SessionConnectEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Collections;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketObj webSocketObj;
    private final AuthService authService;
    private final SecurityDefinitionService securityDefinitionService;

    private String sessionId;

    @MessageMapping("/auth")
    @SendTo("/auth")
    public AuthResponse auth(AuthRequest request, SimpMessageHeaderAccessor headerAccessor) {
        boolean isAuthenticated = authService.auth(request.getToken());
        webSocketObj.setAuth(isAuthenticated);
        webSocketObj.setId(sessionId);
        return new AuthResponse(request.getName() + (isAuthenticated ? ", you are authenticated!" : ", password is incorrect"));
    }

    @MessageMapping("/sdef")
    @SendTo("/sdef")
    public SecurityDefinitionResponse securityDefinitionHandler() {
        if (webSocketObj.isAuth()) {
            return new SecurityDefinitionResponse(securityDefinitionService.getSecurityDefinitions());
        }
        return new SecurityDefinitionResponse(Collections.emptyList());
    }

    @EventListener(SessionConnectEvent.class)
    public void connectHandler(SessionConnectEvent event) {
        String sessionId = event.getWebSocketSession().getId();
        log.debug("Session ID {} connecting", sessionId);

        this.sessionId = sessionId;
    }

    @EventListener(SessionConnectedEvent.class)
    public void connectedHandler(SessionConnectedEvent event) {
        log.debug("Session ID {} connecting", event.toString());
    }

    @EventListener(SessionDisconnectEvent.class)
    public void disconnectHandler(SessionDisconnectEvent event) {
        log.debug("Session ID {} disconnected", event.getSessionId());
    }

}
