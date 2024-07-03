package org.xdev100.vinimay.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FillOrderResult {
    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private double executedQty;

    @Getter
    @Setter
    private List<Fill> fills;

    public FillOrderResult(String status , double executedQty, List<Fill> fills) {
        this.status = status;
        this.executedQty = executedQty;
        this.fills = fills;
    }
}
