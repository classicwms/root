package com.tekclover.wms.api.idmaster.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        //registry.addEndpoint("/notification");
        // registry.addEndpoint("/notification").setAllowedOrigins("/**").withSockJS();;
    	registry.addEndpoint("/notification").setAllowedOrigins("http://3.109.198.223:8080","http://3.111.203.218:8080","http://localhost:4200","https://d.classicwms.com","https://qa.classicwms.com").withSockJS();
    }
}