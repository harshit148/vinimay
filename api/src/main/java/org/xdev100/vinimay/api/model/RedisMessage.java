package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisMessage {
    private String clientId;
    private MessageToEngine message;

    public RedisMessage(String clientId, MessageToEngine message) {
        this.clientId = clientId;
        this.message = message;
    }
}

