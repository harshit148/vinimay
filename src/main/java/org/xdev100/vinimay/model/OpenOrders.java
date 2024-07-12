package org.xdev100.vinimay.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenOrders extends MessageFromOrderBook{
    private String orderId;
    private double executedQuantity;
    private double price;
    private double quantity;
    private OrderSide side;
    private long userId;
}
