package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fill {
    private double price;
    private double qty;
    private long tradeId;
    private String otherUserId;
    private String marketOrderId;
    public Fill() {

    }

    public Fill(double price, double qty, long tradeId) {
        this.price = price;
        this.qty = qty;
        this.tradeId = tradeId;
    }
    public Fill(double price, double qty, long tradeId, String otherUserId, String marketOrderId) {
        this.price = price;
        this.qty = qty;
        this.tradeId = tradeId;
        this.otherUserId = otherUserId;
        this.marketOrderId = marketOrderId;
    }

}
