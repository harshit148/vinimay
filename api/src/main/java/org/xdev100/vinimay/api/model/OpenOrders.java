package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenOrders extends MessageFromOrderBook{
    List<Order> orders;
    /*private String orderId;
    private double executedQuantity;
    private double price;
    private double quantity;
    private OrderSide side;
    private long userId;*/
}
