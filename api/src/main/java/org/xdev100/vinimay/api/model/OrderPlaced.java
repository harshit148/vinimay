package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderPlaced extends MessageFromOrderBook{
    private String orderId;
    private double executedQuantity;
    List<Fill> fills;
}
