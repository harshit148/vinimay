package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FillOrderResult {
    private String status;
    private double executedQty;
    private List<Fill> fills;
    private String orderId;

    public FillOrderResult(String status , double executedQty, List<Fill> fills) {
        this.status = status;
        this.executedQty = executedQty;
        this.fills = fills;
    }
}
