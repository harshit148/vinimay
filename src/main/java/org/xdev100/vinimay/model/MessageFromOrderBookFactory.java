package org.xdev100.vinimay.model;

import java.util.List;

public class MessageFromOrderBookFactory {

    public static DepthResponse createDepthResponse(String market, List<String[]> bids, List<String[]>asks)  {
        DepthResponse depthResponse = new DepthResponse();
        depthResponse.setMarket(market);
        depthResponse.setBids(bids);
        depthResponse.setAsks(asks);
        return depthResponse;
    }
    public static OpenOrders createOpenOrders(String orderId, double executedQuantity, double price, double quantity, OrderSide side, long userId) {
        OpenOrders openOrders = new OpenOrders();
        openOrders.setOrderId(orderId);
        openOrders.setExecutedQuantity(executedQuantity);
        openOrders.setSide(side);
        openOrders.setPrice(price);
        openOrders.setQuantity(quantity);
        openOrders.setUserId(userId);
        return openOrders;
    }
    public static OrderPlaced createOrderPlaced(String orderId, double executedQuantity, List<Fill> fills) {
        OrderPlaced orderPlaced = new OrderPlaced();
        orderPlaced.setOrderId(orderId);
        orderPlaced.setExecutedQuantity(executedQuantity);
        orderPlaced.setFills(fills);
        return orderPlaced;
    }
    public static OrderCancelled createOrderCancelled(String orderId, double executedQuantity, double remainingQuantity) {
        OrderCancelled orderCancelled = new OrderCancelled();
        orderCancelled.setOrderId(orderId);
        orderCancelled.setExecutedQuantity(executedQuantity);
        orderCancelled.setRemainingQuantity(remainingQuantity);
        return orderCancelled;
    }
}
