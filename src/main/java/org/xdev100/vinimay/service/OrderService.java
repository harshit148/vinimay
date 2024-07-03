package org.xdev100.vinimay.service;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xdev100.vinimay.model.*;


@Service
@Slf4j
public class OrderService {
    private static final String BASE_ASSET = "BTC";
    private static final String QUOTE_ASSET = "USD";
    private long globalTradeId = 0;
    private OrderBook orderBook = OrderBook.getOrderBook();

    public OrderResponse processOrder(OrderInputSchema order) {
        if (!order.getBaseAsset().equals(BASE_ASSET)) {
            throw new IllegalArgumentException("Invalid base asset");
        }
        if (!order.getQuoteAsset().equals(QUOTE_ASSET)) {
            throw new IllegalArgumentException("Invalid quote asset");
        }
        String orderId = UUID.randomUUID().toString();
        FillOrderResult fillOrderResult = fillOrder(orderId, order.getPrice(), order.getQuantity(), order.getSide(), order.getKind());
        log.info("Asks: ");
        orderBook.getAsks().forEach(ask -> log.info(ask.toString()));
        log.info("Bids: ");
        orderBook.getBids().forEach(bid -> log.info(bid.toString()));
        log.info("AskBook: " + orderBook.getBookWithQuantity().getAsks());
        log.info("BidBook: " + orderBook.getBookWithQuantity().getBids());
        return new OrderResponse(orderId, fillOrderResult.getExecutedQty(), fillOrderResult.getFills());
    }

    public FillOrderResult fillOrder(String orderId, double price, double quantity, OrderSide side, OrderKind kind) {
        List<Fill> fills = new ArrayList<>();
        double executedQuantity = 0;
        double maxFillQuantity = getFillAmount(price, quantity, side);
        log.info("Order side: " + side);
        if (kind == OrderKind.IOC && maxFillQuantity < quantity) {
            return new FillOrderResult("rejected", maxFillQuantity, null);
        }
        if (side == OrderSide.BUY) {
            for (Order order : orderBook.getAsks()) {
                if (order.getPrice() <= price && quantity > 0 ) {
                    log.info("Filling ask");
                    double filledQuantity = Math.min(quantity, order.getQuantity());
                    log.info("Filled quantity: " + filledQuantity);
                    order.setQuantity(order.getQuantity()-filledQuantity);
                    orderBook.updateBookQuantity(side, order.getPrice(), -filledQuantity);
                    fills.add(new Fill(order.getPrice(), filledQuantity, globalTradeId++));
                    executedQuantity += filledQuantity;
                    quantity -= filledQuantity;
                    if (orderBook.getBookWithQuantity().getAsks().getOrDefault(price, 0.0) == 0) {
                        orderBook.getBookWithQuantity().getAsks().remove(price);
                    }
                    if (order.getQuantity() == 0) {
                        orderBook.getAsks().remove(order);
                    }
                }
            }
            if (quantity != 0) {
                orderBook.getBids().add(new Order(price, quantity-executedQuantity, side, orderId));
                orderBook.updateBookQuantity(side, price, quantity-executedQuantity);
            }
        }
        else if (side == OrderSide.SELL)  {
            for (Order order: orderBook.getBids()) {
                if (order.getPrice() >= price && quantity > 0) {
                    log.info("Filling bid");
                    double filledQuantity = Math.min(order.getQuantity(), quantity);
                    log.info("Filled quantity: "+ filledQuantity);
                    order.setQuantity(order.getQuantity()-filledQuantity);
                    orderBook.updateBookQuantity(side, price, -filledQuantity);
                    fills.add(new Fill(order.getPrice(), filledQuantity, globalTradeId++));
                    executedQuantity += filledQuantity;
                    quantity -=  filledQuantity;
                    if (order.getQuantity() == 0) {
                        orderBook.getBids().remove(order);
                    }
                    if (orderBook.getBookWithQuantity().getBids().getOrDefault(price, 0.0) == 0) {
                        orderBook.getBookWithQuantity().getBids().remove(price);
                    }
                }
            }
            if (quantity != 0 ){
                orderBook.getAsks().add(new Order(price, quantity-executedQuantity, side, orderId));
                orderBook.updateBookQuantity(side, price, quantity-executedQuantity);
            }
        }
        return new FillOrderResult("accepted", executedQuantity, fills);
    }

    public double getFillAmount(double price, double quantity, OrderSide side) {
        if (side == OrderSide.BUY) {
            return orderBook.getAsks().stream().filter(ask -> ask.getPrice() < price)
                    .mapToDouble(ask -> Math.min(ask.getQuantity(), quantity))
                    .sum();
        }
        if (side == OrderSide.SELL) {
            return orderBook.getBids().stream().filter(bid -> bid.getPrice() > price)
                    .mapToDouble(bid -> Math.min(quantity, bid.getQuantity()))
                    .sum();
        }
        return 0;
    }
}
