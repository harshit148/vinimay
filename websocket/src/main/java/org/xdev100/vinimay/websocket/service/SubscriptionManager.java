package org.xdev100.vinimay.websocket.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@Service
@Slf4j
public class SubscriptionManager {

    @Getter
    private static SubscriptionManager instance;
    private final RedisTemplate<String, String> redisTemplate;
    //private RedisMessageListenerContainer redisMessageListenerContainer;
    private UserManager userManager;
    private Map<String, List<String>> subscriptions = new HashMap<>();
    private Map<String, List<String>> reverseSubscriptions = new HashMap<>();

    @Autowired
    public SubscriptionManager(RedisTemplate <String, String> redisTemplate, UserManager userManager) {
        this.redisTemplate = redisTemplate;
        this.userManager = userManager;
        //this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    public void subscribe(String userId, String subscription) {
        if (this.subscriptions.containsKey(userId) && this.subscriptions.get(userId).contains(subscription)) {
            return;
        }
        this.subscriptions.computeIfAbsent(userId, k -> new ArrayList<>()).add(subscription);
        this.reverseSubscriptions.computeIfAbsent(subscription, k -> new ArrayList<>()).add(userId);
        if (this.reverseSubscriptions.get(subscription).size() == 1) {
            redisTemplate.getConnectionFactory().getConnection().subscribe((message, pattern) -> {
                String channel = new String(message.getChannel());
                String parsedMessage = new String(message.getBody());
                reverseSubscriptions.get(channel).forEach(user -> {
                    UserManager.getInstance().getUser(user).emit(parsedMessage);
                });
            }, subscription.getBytes());
        }

    }

    public void unsubscribe(String userId, String subscription) {
        List<String> userSubscriptions = this.subscriptions.get(userId);
        if (userSubscriptions != null) {
            userSubscriptions.remove(subscription);
            if (userSubscriptions.isEmpty()){
                this.subscriptions.remove(userId);
            }
        }
        List<String> subscribers = this.reverseSubscriptions.get(subscription);
        if (subscribers != null) {
            subscribers.remove(userId);
            if(subscribers.isEmpty()) {
                this.reverseSubscriptions.remove(subscription);
                redisTemplate.getConnectionFactory().getConnection().getSubscription().unsubscribe(subscription.getBytes());
            }
        }
    }
    public void userLeft(String userId) {
        log.info("User left: "+ userId);
        subscriptions.get(userId).forEach(subscription -> unsubscribe(userId, subscription));
    }
    public List<String> getSubscription(String userId) {
        return subscriptions.getOrDefault(userId, Collections.emptyList());
    }
}
