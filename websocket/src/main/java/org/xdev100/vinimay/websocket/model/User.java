package org.xdev100.vinimay.websocket.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.xdev100.vinimay.websocket.service.SubscriptionManager;
import org.xdev100.vinimay.websocket.service.UserManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        String payload = ((TextMessage)message).getPayload();
        IncomingMessage incomingMessage = new ObjectMapper().readValue(payload, IncomingMessage.class);

        if (incomingMessage.getMethod().equals("SUBSCRIBE")) {
            log.info("SUBSCRIBE MESSAGE RECEIVED");
            SubscribeMessage subscribeMessage = (SubscribeMessage) incomingMessage;
            for (String subscription : subscribeMessage.getParams()) {
                log.info("Subscription: "+ subscription);
                SubscriptionManager.getInstance().subscribe(session.getId(), subscription);
            }
        } else if (incomingMessage.getMethod().equals("UNSUBSCRIBE")) {
            log.info("UNSUBSCRIBE MESSAGE RECEIVED");
            UnsubscribeMessage unsubscribeMessage = (UnsubscribeMessage) incomingMessage;
            unsubscribeMessage.getParams().forEach(s -> SubscriptionManager.getInstance().unsubscribe(session.getId(), unsubscribeMessage.getParams().get(0)));
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession ws, CloseStatus status) {
        User user = UserManager.getInstance().getUser(ws.getId());
        if (user != null) {
            UserManager.getInstance().getUsers().remove(ws.getId());
            SubscriptionManager.getInstance().userLeft(ws.getId());
        }

    }
}
