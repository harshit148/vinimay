package org.xdev100.vinimay.websocket.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.xdev100.vinimay.websocket.service.SubscriptionManager;
import org.xdev100.vinimay.websocket.service.UserManager;

import java.util.ArrayList;
import java.util.List;

public class User extends TextWebSocketHandler {
    private String id;
    private WebSocketSession websocket;
    private List<String> subscriptions;
    private final ObjectMapper objectMapper;

    public User() {
        objectMapper = new ObjectMapper();
    }

    public User(String id, WebSocketSession websocket) {
        this.id = id;
        this.websocket = websocket;
        objectMapper = new ObjectMapper();
    }

    public void subscribe(String subscription) {
        if (subscriptions.isEmpty()) {
            subscriptions = new ArrayList<>();
        }
        subscriptions.add(subscription);
    }
    public void unsubscribe(String subscription) {
        subscriptions.remove(subscription);
    }
    public void emit(String message) {
        try {
            websocket.sendMessage(new TextMessage(message));
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserManager.getInstance().addUser(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage message) throws Exception {
        String payload = message.toString();
        IncomingMessage incomingMessage = new ObjectMapper().readValue(payload, IncomingMessage.class);

        if (incomingMessage.getMethod().equals("SUBSCRIBE")) {
            SubscribeMessage subscribeMessage = (SubscribeMessage) incomingMessage;
            for (String subscription : subscribeMessage.getParams()) {
                SubscriptionManager.getInstance().subscribe(id, subscription);
            }
        } else if (incomingMessage.getMethod().equals("UNSUBSCRIBE")) {
            UnsubscribeMessage unsubscribeMessage = (UnsubscribeMessage) incomingMessage;
            unsubscribeMessage.getParams().forEach(s -> SubscriptionManager.getInstance().unsubscribe(id, unsubscribeMessage.getParams().get(0)));
        }
    }
}
