package org.xdev100.vinimay.websocket.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "method")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SubscribeMessage.class, name="SUBSCRIBE"),
        @JsonSubTypes.Type(value = UnsubscribeMessage.class, name="UNSUBSCRIBE")
})
public abstract class IncomingMessage {
    private String method;
    public List<String> params;
}
