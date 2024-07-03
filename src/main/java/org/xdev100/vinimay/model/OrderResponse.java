package org.xdev100.vinimay.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class OrderResponse {

    @Getter
    @Setter
    private String orderId;

    @Getter
    @Setter
    private double executedQuantity;

    @Getter
    @Setter
    private List<Fill> fills;

    public OrderResponse(String orderId, double executedQuantity, List<Fill> fills) {
        this.orderId = orderId;
        this.executedQuantity = executedQuantity;
        this.fills = fills;
    }

}
