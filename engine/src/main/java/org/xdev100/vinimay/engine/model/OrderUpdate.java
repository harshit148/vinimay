package org.xdev100.vinimay.engine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.xdev100.vinimay.api.model.OrderSide;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdate extends DatabaseMessage{
    private String orderId;
    private double executedQty;
    private OrderSide side;
}
