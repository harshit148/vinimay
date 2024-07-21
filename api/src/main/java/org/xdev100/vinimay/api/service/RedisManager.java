package org.xdev100.vinimay.api.service;


import lombok.extern.slf4j.Slf4j;
import org.xdev100.vinimay.api.model.MessageToEngine;
import org.xdev100.vinimay.api.model.MessageFromOrderBook;
import org.xdev100.vinimay.api.model.RedisMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

@Service
@Slf4j
public class RedisManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentMap<String, CompletableFuture<MessageFromOrderBook>> pendingMessages = new ConcurrentHashMap<>();

    @Autowired
    public RedisManager(RedisTemplate<String, Object> redisTemplate, RedisMessageListenerContainer redisMessageListenerContainer) {
        this.redisTemplate = redisTemplate;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    public CompletableFuture<MessageFromOrderBook> sendAndAwait(MessageToEngine message) throws JsonProcessingException {
        log.info("Reached sendAndAwait");
        CompletableFuture<MessageFromOrderBook> future = new CompletableFuture<>();
        String clientId = UUID.randomUUID().toString();
        pendingMessages.put(clientId, future);
        redisMessageListenerContainer.addMessageListener(new MessageListenerAdapter() {
            @Override
            public void onMessage(Message message1, byte[] pattern) {
                String msg = new String(message1.getBody());
                System.out.println("Response from engine: "+ msg);
                try {
                    MessageFromOrderBook response = objectMapper.readValue(msg, MessageFromOrderBook.class);
                    pendingMessages.get(clientId).complete(response);
                    pendingMessages.remove(clientId);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }, new ChannelTopic(clientId));
        log.info("Post message listener container");
        redisTemplate.opsForList().leftPush("messages", objectMapper.writeValueAsString(new RedisMessage(clientId, message)));
        return future;
    }
}

