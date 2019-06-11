package com.vvhiterussian.websockets.simple.config;

import com.vvhiterussian.websockets.simple.interceptors.FilterChannelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer {

    private static final int HEARTBEAT_TASK_POOL_SIZE = 10;
    private static final String HEARTBEAT_TASK_POOL_PREFIX = "heartbeats-thread-pool";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/auth", "/sdef")
                .setTaskScheduler(heartbeatsTaskScheduler());
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new FilterChannelInterceptor());
        super.configureClientInboundChannel(registration);
    }

    @Bean
    public TaskScheduler heartbeatsTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix(HEARTBEAT_TASK_POOL_PREFIX);
        taskScheduler.setPoolSize(HEARTBEAT_TASK_POOL_SIZE);
        taskScheduler.initialize();
        return taskScheduler;
    }
}
