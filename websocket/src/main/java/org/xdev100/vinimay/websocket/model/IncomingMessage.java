package org.xdev100.vinimay.websocket.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class IncomingMessage {
    private String method;
    public List<String> params;
}
