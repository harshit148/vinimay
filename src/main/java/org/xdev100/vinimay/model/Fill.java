package org.xdev100.vinimay.model;

import lombok.Getter;
import lombok.Setter;

public class Fill {
    @Getter
    @Setter
    private double price;

    @Setter
    @Getter
    private double qty;

    @Setter
    @Getter
    private long tradeId;

    public Fill(double price, double qty, long tradeId) {
        this.price = price;
        this.qty = qty;
        this.tradeId = tradeId;
    }
}
