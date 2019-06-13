package com.vvhiterussian.websockets.simple.service;

import com.vvhiterussian.websockets.simple.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SimpMessagingTemplate brokerMessagingTemplate;

    private Map<String, Set<Subscription>> subscriptions = new ConcurrentHashMap<>();
    private Set<String> destinations = new HashSet<>();
    private AtomicBoolean broadcast = new AtomicBoolean(false);

    public void addSubscription(Subscription subscription) {
        if (!subscriptions.containsKey(subscription.getSessionId())) {
            subscriptions.put(subscription.getSessionId(), new HashSet<>());
        }

        subscriptions.get(subscription.getSessionId()).add(subscription);

        destinations.add(subscription.getDestination());

        if (!broadcast.get()) {
            broadcast.set(true);
        }
    }

    public void removeSubscription(Subscription subscription) {
        if (subscriptions.containsKey(subscription.getSessionId())) {
            subscriptions.get(subscription.getSessionId()).remove(subscription);

            if (isLastSubscriptionForDestination(subscription.getDestination())) {
                destinations.remove(subscription.getDestination());
            }
        }
    }

    public void removeSubscription(String sessionId, String subscriptionId) {
        if (subscriptions.containsKey(sessionId)) {
            subscriptions.get(sessionId).stream()
                    .filter(sub -> sub.getSubsriptionId().equals(subscriptionId))
                    .findFirst()
                    .ifPresent(this::removeSubscription);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void broadcast() {
        if (broadcast.get()) {
            destinations.forEach(destination -> {
                String message = "Message motherfucker!";
                brokerMessagingTemplate.convertAndSend(destination, message);
            });
        }
    }

    private boolean isLastSubscriptionForDestination(String destination) {
        for (Set<Subscription> set : subscriptions.values()) {
            for (Subscription sub : set) {
                if (sub.getDestination().equals(destination)) {
                    return false;
                }
            }
        }
        return true;
    }

    void start() {
        broadcast.set(true);
    }

    void stop() {
        broadcast.set(false);
    }

    public boolean isStarted() {
        return broadcast.get();
    }
}
