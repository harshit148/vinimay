package org.xdev100.vinimay.model;

import lombok.Getter;
import lombok.Setter;

public class Order {

    @Getter
    @Setter
    private double price;

    @Getter
    @Setter
    private double quantity;

    @Getter
    @Setter
    private OrderSide side;

    @Getter
    @Setter
    private String orderId;

    public Order(double price, double quantity, OrderSide side, String orderId) {
        this.price = price;
        this.side = side;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Quantity: " + quantity + ", price: " + price;
    }
}
