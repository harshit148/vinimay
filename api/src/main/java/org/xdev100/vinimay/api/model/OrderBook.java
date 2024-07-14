package org.xdev100.vinimay.api.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class OrderBook {
    private static OrderBook instance;
    @Getter
    private List<Order> bids;
    @Getter
    private  List<Order> asks;
    @Getter
    private BookWithQuantity bookWithQuantity;

    private OrderBook() {
        this.bids = new ArrayList<>();
        this.asks = new ArrayList<>();
        this.bookWithQuantity = new BookWithQuantity();
    }

    public static OrderBook getOrderBook() {
        if (instance == null) {
            instance = new OrderBook();
        }
        return instance;
    }
    public void updateBookQuantity(OrderSide side, double price, double quantity) {
        if (side == OrderSide.BUY) {
            bookWithQuantity.getBids().put(price, bookWithQuantity.getBids().getOrDefault(price, 0.0) + quantity);
        }
        else if (side == OrderSide.SELL) {
            bookWithQuantity.getAsks().put(price, bookWithQuantity.getAsks().getOrDefault(price, 0.0) + quantity);
        }
        if ( bookWithQuantity.getBids().containsKey(price) && bookWithQuantity.getBids().get(price) == 0) {
            bookWithQuantity.getBids().remove(price);
        }
        if (bookWithQuantity.getAsks().containsKey(price) && bookWithQuantity.getAsks().get(price) == 0 ){
            bookWithQuantity.getAsks().remove(price);
        }
    }

}
