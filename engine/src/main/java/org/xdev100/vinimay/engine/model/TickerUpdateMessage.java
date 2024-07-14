package org.xdev100.vinimay.engine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TickerUpdateMessage extends WebSocketMessage{
    private String c;
    private String h;
    private String l;
    private String v;
    private String V;
    private String s;
    private long id;
}
