package org.xdev100.vinimay.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCancelled extends MessageFromOrderBook {
    private String orderId;
    private double executedQuantity;
    private double remainingQuantity;
}
