package org.xdev100.vinimay.websocket.config;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.xdev100.vinimay.websocket.service.SubscriptionManager;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

   /* @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory, SubscriptionManager subscriptionManager) {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(redisConnectionFactory);
        redisContainer.addMessageListener((MessageListener) subscriptionManager, new PatternTopic("trades@*"));
        redisContainer.addMessageListener((MessageListener) subscriptionManager, new PatternTopic("depth@*"));
        redisContainer.addMessageListener((MessageListener) subscriptionManager, new PatternTopic("ticker@*"));
        return redisContainer;
    }*/
}
