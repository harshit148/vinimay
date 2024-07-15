package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
public class Order {

    private double price;
    private double quantity;
    private OrderSide side;
    private String orderId;
    private double filled;
    private String userId;

    public Order(double price, double quantity, OrderSide side, String orderId) {
        this.price = price;
        this.side = side;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    public Order(double price, double quantity, OrderSide side, String orderId, double filled, String userId) {
        this.price = price;
        this.side = side;
        this.quantity = quantity;
        this.orderId = orderId;
        this.filled = filled;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Quantity: " + quantity + ", price: " + price;
    }
}
