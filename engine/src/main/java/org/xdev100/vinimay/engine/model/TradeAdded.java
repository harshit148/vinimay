package org.xdev100.vinimay.engine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TradeAdded extends DatabaseMessage{
    private String id;
    private boolean isBuyerMaker;
    private double quoteQuantity;
    private long timestamp;
}
