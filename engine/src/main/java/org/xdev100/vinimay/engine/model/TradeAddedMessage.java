package org.xdev100.vinimay.engine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeAddedMessage extends WebSocketMessage{
    private double t;
    private boolean m;
    private String p;
    private String q;
    private String s;
}
