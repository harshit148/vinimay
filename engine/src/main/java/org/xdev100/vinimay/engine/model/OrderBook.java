package org.xdev100.vinimay.engine.model;

import lombok.Getter;
import lombok.Setter;
import org.xdev100.vinimay.api.model.*;

import java.util.*;

@Getter
@Setter
public class OrderBook {
    private List<Order> bids;
    private List<Order> asks;
    private String baseAsset;
    private String quoteAsset;
    private long lastTradeId;
    private double currentPrice;

    public OrderBook(String baseAsset, List<Order> bids, List<Order> asks, Long lastTradeId, Double currentPrice) {
        this.bids = bids;
        this.asks = asks;
        this.baseAsset = baseAsset;
        this.lastTradeId = (lastTradeId != null) ? lastTradeId: 0L;
        this.currentPrice = (currentPrice != null) ? currentPrice: 0.0;
    }

    public String ticker() {
        return baseAsset + "_" + quoteAsset;
    }
    public OrderBook getSnapShot() {
        /*Map <String, Object> snapShot = new HashMap<>();
        snapShot.put("baseAsset", baseAsset);
        snapShot.put("bids", bids);
        snapShot.put("asks", asks);
        snapShot.put("lastTradeId", lastTradeId);
        snapShot.put("currentPrice", currentPrice);
        return snapShot;*/
        return new OrderBook(baseAsset, bids, asks, lastTradeId, currentPrice);
    }
    public FillOrderResult addOrder(Order order) {
        if (order.getSide() == OrderSide.BUY) {
            FillOrderResult orderResult = matchBid(order);
            order.setFilled(orderResult.getExecutedQty());
            if (orderResult.getExecutedQty() == order.getQuantity()) {
                return orderResult;
            }
            this.bids.add(order);
            return orderResult;
        }
        else {
            FillOrderResult orderResult = matchAsk(order);
            order.setFilled(orderResult.getExecutedQty());
            if (orderResult.getExecutedQty() == order.getQuantity()) {
                return orderResult;
            }
            this.asks.add(order);
            return orderResult;
        }
    }
    public FillOrderResult matchBid(Order order) {
        List<Fill> fills = new ArrayList<>();
        double executedQuantity = 0;
        for(Order ask : asks) {
            if (executedQuantity == order.getQuantity()) break;
            if (ask.getPrice() <= order.getPrice() && executedQuantity < order.getQuantity()) {
                double filledQuantity = Math.min((order.getQuantity()-executedQuantity), ask.getQuantity());
                executedQuantity += filledQuantity;
                ask.setFilled(ask.getFilled()+filledQuantity);
                fills.add(new Fill(ask.getPrice(), filledQuantity, lastTradeId++, ask.getUserId(), ask.getOrderId()));
            }
        }
        asks.removeIf(ask -> ask.getFilled() == ask.getQuantity());
        return new FillOrderResult("accepted", executedQuantity, fills);
    }
    public FillOrderResult matchAsk(Order order) {
        List <Fill> fills = new ArrayList<>();
        double executedQuantity = 0;
        for (Order bid: bids) {
            if (executedQuantity == order.getQuantity()) break;
            if (bid.getPrice() >= order.getPrice()  && executedQuantity < order.getQuantity()) {
                double amountRemaining = Math.min((order.getQuantity()-executedQuantity), bid.getQuantity());
                executedQuantity += amountRemaining;
                bid.setFilled(bid.getFilled()+amountRemaining);
                fills.add(new Fill(bid.getPrice(), amountRemaining, lastTradeId++, bid.getUserId(), bid.getOrderId()));
            }
        }
        bids.removeIf(bid -> bid.getFilled()==bid.getQuantity());
        return new FillOrderResult("accepted", executedQuantity, fills);
    }
    public DepthResponse getDepth() {
        List <String[]> depthBids = new ArrayList<>();
        List <String[]> depthAsks = new ArrayList<>();
        Map <Double, Double> bidMap = new HashMap<>();
        Map <Double, Double> askMap = new HashMap<>();
        for (Order bid: bids) {
            bidMap.put(bid.getPrice(), bidMap.getOrDefault(bid.getPrice(), 0.0) + bid.getQuantity());
        }
        for (Order ask: asks) {
            askMap.put(ask.getPrice(), askMap.getOrDefault(ask.getPrice(), 0.0) + ask.getQuantity());
        }
        for (Map.Entry<Double, Double> element: bidMap.entrySet()) {
            depthBids.add(new String[]{String.valueOf(element.getKey()), String.valueOf(element.getValue())});
        }
        for (Map.Entry<Double, Double> element: askMap.entrySet()) {
            depthAsks.add(new String[]{String.valueOf(element.getKey()), String.valueOf(element.getValue())});
        }
        DepthResponse depthResponse = new DepthResponse();
        depthResponse.setAsks(depthAsks);
        depthResponse.setBids(depthBids);
        return depthResponse;
    }
    public OpenOrders getOpenOrders(String userId) {
        OpenOrders openOrders =  new OpenOrders();
        List <Order> asks = this.asks.stream().filter(ask -> ask.getUserId().equals(userId)).toList();
        List <Order> bids = this.bids.stream().filter(bid -> bid.getUserId().equals(userId)).toList();
        openOrders.setOrders(asks);
        openOrders.getOrders().addAll(bids);
        return openOrders;
    }

    public double cancelBid(Order order) {
        return bids.stream().filter(bid -> bid.getOrderId().equals(order.getOrderId())).findFirst().get().getPrice();
    }
    public double cancelAsk(Order order) {
        return asks.stream().filter(ask -> ask.getOrderId().equals(order.getOrderId())).findFirst().get().getPrice();
    }
}
