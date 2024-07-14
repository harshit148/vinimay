package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderResponse {

    private String orderId;

    private double executedQuantity;

    private List<Fill> fills;

    public OrderResponse(String orderId, double executedQuantity, List<Fill> fills) {
        this.orderId = orderId;
        this.executedQuantity = executedQuantity;
        this.fills = fills;
    }

}
