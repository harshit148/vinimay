package org.xdev100.vinimay.websocket.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnsubscribeMessage extends IncomingMessage{
    private String method = "UNSUBSCRIBE";
}
