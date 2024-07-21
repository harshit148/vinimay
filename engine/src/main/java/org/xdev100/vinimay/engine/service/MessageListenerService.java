package org.xdev100.vinimay.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.xdev100.vinimay.api.model.RedisMessage;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;


@Service
@Slf4j
public class MessageListenerService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Engine engine;
    private final ObjectMapper jacksonObjectMapper;

    @Autowired
    public MessageListenerService(RedisTemplate<String, String> redisTemplate, Engine engine, ObjectMapper jacksonObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.engine = engine;
        this.jacksonObjectMapper = jacksonObjectMapper;
    }
    public void listenForMessages() {
        new Thread(() -> {
            while(true) {
                String message = String.valueOf(redisTemplate.opsForList().rightPop("messages"));
                //System.out.println("Message receieved: " + message);
                if (!message.equals("null")) {
                    try {
                        RedisMessage redisMessage = jacksonObjectMapper.readValue(message, RedisMessage.class);
                        System.out.println("Redis message received");
                        System.out.println(redisMessage);
                        System.out.println(redisMessage.getMessage().getType());
                        engine.processMessage(redisMessage.getMessage(), redisMessage.getClientId());
                    } catch (JsonProcessingException e) {
                        System.out.println("Parsing");
                    }

                }
            }
        }).start();
    }


}
