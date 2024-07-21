package org.xdev100.vinimay.websocket.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.xdev100.vinimay.websocket.model.User;

import java.util.*;

@Service
@Slf4j
public class SubscriptionManager {

    @Getter
    private static SubscriptionManager instance;
    private final RedisTemplate<String, String> redisTemplate;
    private RedisMessageListenerContainer redisMessageListenerContainer;
    private UserManager userManager;
    private Map<String, List<String>> subscriptions = new HashMap<>();
    private Map<String, List<String>> reverseSubscriptions = new HashMap<>();

    @Autowired
    public SubscriptionManager(RedisTemplate <String, String> redisTemplate, UserManager userManager, RedisMessageListenerContainer redisContainer) {
        this.redisTemplate = redisTemplate;
        this.userManager = userManager;
        this.redisMessageListenerContainer = redisContainer;
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
            redisMessageListenerContainer.addMessageListener((message, pattern) -> {
                log.info("Message received");
                String channel = new String(message.getChannel());
                String body = new String(message.getBody());
                log.info("Channel: "+ channel);
                log.info("Body: "+ body);
                List<String> subscribers = reverseSubscriptions.get(channel);
                log.info("Subscribers: "+ reverseSubscriptions);
                if (subscribers != null) {
                    for (String subscriberId : subscribers) {
                        User user = UserManager.getInstance().getUser(subscriberId);
                        if (user != null) {
                            user.emit(body);
                        }
                    }
                }
            }, new ChannelTopic(subscription));
            log.info("User " + userId + " is subscribing to " + subscription);
            log.info("Subscriptions: "+ subscriptions);
            log.info("Subscribers: "+ reverseSubscriptions);
            /*redisTemplate.getConnectionFactory().getConnection().subscribe((message, pattern) -> {
                String channel = new String(message.getChannel());
                String parsedMessage = new String(message.getBody());
                reverseSubscriptions.get(channel).forEach(user -> {
                    UserManager.getInstance().getUser(user).emit(parsedMessage);
                });
            }, subscription.getBytes());*/
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
                redisMessageListenerContainer.removeMessageListener((message, pattern) -> {
                    // No-op
                }, new ChannelTopic(subscription));
                //redisTemplate.getConnectionFactory().getConnection().getSubscription().unsubscribe(subscription.getBytes());
            }
        }
        log.info("User " + userId + " is unsubscribing from " + subscription);
        log.info("Subscriptions: "+ subscriptions);
    }
    public void userLeft(String userId) {
        log.info("User left: "+ userId);
        subscriptions.get(userId).forEach(subscription -> unsubscribe(userId, subscription));
    }
    public List<String> getSubscription(String userId) {
        return subscriptions.getOrDefault(userId, Collections.emptyList());
    }
}

/*

package org.xdev100.vinimay.websocket.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
@Slf4j
public class SubscriptionManager {

    @Getter
    private static SubscriptionManager instance;

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private UserManager userManager;

    private Map<String, List<String>> subscriptions = new HashMap<>();
    private Map<String, List<String>> reverseSubscriptions = new HashMap<>();

    @Autowired
    public SubscriptionManager(RedisTemplate<String, String> redisTemplate,
                               RedisMessageListenerContainer redisMessageListenerContainer,
                               UserManager userManager) {
        this.redisTemplate = redisTemplate;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
        this.userManager = userManager;
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    public void subscribe(String userId, String subscription) {
        // Add subscription for user
        if (!subscriptions.containsKey(userId)) {
            subscriptions.put(userId, new ArrayList<>());
        }
        List<String> userSubscriptions = subscriptions.get(userId);
        if (!userSubscriptions.contains(subscription)) {
            userSubscriptions.add(subscription);
        }

        // Add user to reverse subscriptions map
        reverseSubscriptions.computeIfAbsent(subscription, k -> new ArrayList<>()).add(userId);

        // Subscribe to Redis channel if this is the first subscription
        if (reverseSubscriptions.get(subscription).size() == 1) {
            redisMessageListenerContainer.addMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] pattern) {
                    String channel = new String(message.getChannel());
                    String body = new String(message.getBody());
                    List<String> subscribers = reverseSubscriptions.get(channel);
                    if (subscribers != null) {
                        for (String subscriberId : subscribers) {
                            User user = UserManager.getInstance().getUser(subscriberId);
                            if (user != null) {
                                user.emit(body);
                            }
                        }
                    }
                }
            }, new ChannelTopic(subscription));
        }
    }

    public void unsubscribe(String userId, String subscription) {
        List<String> userSubscriptions = subscriptions.get(userId);
        if (userSubscriptions != null) {
            userSubscriptions.remove(subscription);
            if (userSubscriptions.isEmpty()) {
                subscriptions.remove(userId);
            }
        }

        List<String> subscribers = reverseSubscriptions.get(subscription);
        if (subscribers != null) {
            subscribers.remove(userId);
            if (subscribers.isEmpty()) {
                reverseSubscriptions.remove(subscription);
                redisMessageListenerContainer.removeMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message, byte[] pattern) {
                        // No-op
                    }
                }, new ChannelTopic(subscription));
            }
        }
    }

    public void userLeft(String userId) {
        log.info("User left: " + userId);
        List<String> userSubscriptions = subscriptions.get(userId);
        if (userSubscriptions != null) {
            userSubscriptions.forEach(subscription -> unsubscribe(userId, subscription));
        }
    }

    public List<String> getSubscribers(String subscription) {
        return reverseSubscriptions.getOrDefault(subscription, Collections.emptyList());
    }
}
*/