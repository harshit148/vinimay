package org.xdev100.vinimay.api.model;

import lombok.Getter;

import java.util.Map;
import java.util.HashMap;

public class BookWithQuantity {
    @Getter
    private Map<Double, Double> bids;
    @Getter
    private Map<Double, Double> asks;

    public BookWithQuantity() {
        this.bids = new HashMap<>();
        this.asks = new HashMap<>();
    }

}
