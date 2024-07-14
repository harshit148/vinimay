package org.xdev100.vinimay.websocket.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeMessage extends IncomingMessage{
    private String method = "SUBSCRIBE";
}
