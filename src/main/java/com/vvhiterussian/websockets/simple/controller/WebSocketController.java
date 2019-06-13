package com.vvhiterussian.websockets.simple.controller;

import com.vvhiterussian.websockets.simple.model.*;
import com.vvhiterussian.websockets.simple.service.AuthService;
import com.vvhiterussian.websockets.simple.service.SecurityDefinitionService;
import com.vvhiterussian.websockets.simple.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.session.web.socket.events.SessionConnectEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private static final String SEC_DEF_TOPIC = "/sdef";

    private final WebSocketObj webSocketObj;
    private final AuthService authService;
    private final SecurityDefinitionService securityDefinitionService;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    private final SubscriptionService subscriptionService;

    private String sessionId;
    private Map<String, ScheduledExecutorService> publishingServices = new HashMap<>();


    @SubscribeMapping("/market-data/{idSource}/{securityId}/{exDestination}")
    public void handleSubscriprtion(@DestinationVariable String idSource,
                                    @DestinationVariable String securityId,
                                    @DestinationVariable String exDestination,
                                    SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String subscriptionId = headerAccessor.getSubscriptionId();
        String destination = headerAccessor.getDestination();

        Subscription subscription = new Subscription(sessionId, subscriptionId, destination);
        subscriptionService.addSubscription(subscription);

        log.info("Subscription handled: {} : {} : {}", idSource, securityId, exDestination);
    }

    @MessageMapping("/auth")
    @SendTo("/auth")
    public AuthResponse auth(AuthRequest request, SimpMessageHeaderAccessor headerAccessor) {
        boolean isAuthenticated = authService.auth(request.getToken());
        webSocketObj.setAuth(isAuthenticated);
        webSocketObj.setId(sessionId);
        return new AuthResponse(request.getName() + (isAuthenticated ? ", you are authenticated!" : ", password is incorrect"));
    }

    @MessageMapping(SEC_DEF_TOPIC)
    @SendTo(SEC_DEF_TOPIC)
    public SecurityDefinitionResponse securityDefinitionRequestHandler() {
        if (webSocketObj.isAuth()) {
            return new SecurityDefinitionResponse(securityDefinitionService.getSecurityDefinitions());
        }
        return new SecurityDefinitionResponse(Collections.emptyList());
    }

    @EventListener(SessionSubscribeEvent.class)
    public void subscribeHandler(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
//        String simpDestination = headers.get("simpDestination").toString();
//        String simpSessionId = headers.get("simpSessionId").toString();
//        String simpSubscriptionId = headers.get("simpSubscriptionId").toString();
//        if (SEC_DEF_TOPIC.equals(headers.getDestination())) {
//            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//            publishingServices.put(headers.getSubscriptionId(), service);
//            service.scheduleAtFixedRate(() ->
//                            brokerMessagingTemplate.convertAndSend(SEC_DEF_TOPIC, new SecurityDefinitionResponse(securityDefinitionService.getSecurityDefinitions())),
//                    2, 2, TimeUnit.SECONDS);
//        }
        log.debug("Subscribe {}", headers.getSubscriptionId());
    }

    @EventListener(SessionUnsubscribeEvent.class)
    public void unsubscribeHandler(SessionUnsubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        String subscriptionId = headers.getSubscriptionId();

        subscriptionService.removeSubscription(sessionId, subscriptionId);

//        if (publishingServices.containsKey(headers.getSubscriptionId())) {
//            publishingServices.get(headers.getSubscriptionId()).shutdown();
//        }
        log.debug("Unsubscribe {} ", headers.getSubscriptionId());
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
