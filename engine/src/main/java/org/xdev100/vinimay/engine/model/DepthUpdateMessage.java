package org.xdev100.vinimay.engine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DepthUpdateMessage extends WebSocketMessage{
    private List<Pair<String, String>> a;
    private List<Pair<String, String>> b;
    private long id;

    public DepthUpdateMessage(List<Pair<String, String>> a, List<Pair<String, String>> b) {
        this.a = a;
        this.b = b;
    }

}
