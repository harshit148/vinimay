package org.xdev100.vinimay.websocket.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.xdev100.vinimay.websocket.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserManager {
    @Getter
    private static UserManager instance;
    private Map<String, User> users;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public User addUser(WebSocketSession session) {
        String id = UUID.randomUUID().toString();
        User user = new User(id, session);
        if (users.isEmpty()) {
            users = new HashMap<>();
        }
        users.put(id, user);
        registerOnClose(session, id);
        return user;
    }
    private void registerOnClose(WebSocketSession ws, String userId) {
        if (!ws.isOpen()) {
            users.remove(userId);
            SubscriptionManager.getInstance().userLeft(userId);
        }
    }
    public User getUser(String userId) {
        return users.get(userId);
    }
}
