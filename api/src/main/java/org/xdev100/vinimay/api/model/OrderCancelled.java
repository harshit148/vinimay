package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCancelled extends MessageFromOrderBook {
    private String orderId;
    private double executedQuantity;
    private double remainingQuantity;
}
