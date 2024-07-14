package org.xdev100.vinimay.engine.service;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.xdev100.vinimay.api.model.MessageFromOrderBook;
import org.xdev100.vinimay.engine.model.DatabaseMessage;
import org.xdev100.vinimay.engine.model.WebSocketMessage;

@Service
public class RedisPubSub {

    @Autowired
    private StringRedisTemplate redisClient;

    private static RedisPubSub instance;

    private final ObjectMapper objectMapper;

    @Autowired
    public RedisPubSub(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    public RedisPubSub getInstance() {
        return instance;
    }


    public void pushMessage(DatabaseMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisClient.opsForList().leftPush("db_processor", jsonMessage);
        }catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    public void publishMessage(String channel, WebSocketMessage webSocketMessage) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(webSocketMessage);
            redisClient.convertAndSend(channel, jsonMessage);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    public void sendToApi(String clientId, MessageFromOrderBook messageToApi) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(messageToApi);
            redisClient.convertAndSend(clientId, jsonMessage);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
