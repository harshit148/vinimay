package org.xdev100.vinimay.engine.service;

import org.xdev100.vinimay.api.model.RedisMessage;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class MessageListenerService {

    private final RedisTemplate<String, RedisMessage> redisTemplate;
    private final Engine engine;

    @Autowired
    public MessageListenerService(RedisTemplate<String, RedisMessage> redisTemplate, Engine engine) {
        this.redisTemplate = redisTemplate;
        this.engine = engine;
    }
    public void listenForMessaages() {
        new Thread(() -> {
             RedisMessage redisMessage = redisTemplate.opsForList().rightPop("messages");
            if (redisMessage != null) {
                engine.processMessage(redisMessage.getMessage(), redisMessage.getClientId());
            }
        }).start();
    }
}
