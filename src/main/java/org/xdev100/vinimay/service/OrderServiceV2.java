package org.xdev100.vinimay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.xdev100.vinimay.model.MessageFromOrderBook;
import org.xdev100.vinimay.model.MessageFromOrderBookFactory;
import org.xdev100.vinimay.model.MessageToEngine;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderServiceV2 {

    @Autowired
    private RedisManager redisManager;

    public CompletableFuture<MessageFromOrderBook> createOrder(MessageToEngine.CreateOrder order) throws JsonProcessingException {
        return redisManager.sendAndAwait(MessageToEngine.from(order));
    }
    public CompletableFuture<MessageFromOrderBook> cancelOrder(MessageToEngine.CancelOrder order) throws JsonProcessingException {
        return redisManager.sendAndAwait(MessageToEngine.from(order));
    }
    public CompletableFuture<MessageFromOrderBook> getOpenOrders(MessageToEngine.GetOpenOrder order) throws JsonProcessingException {
        return redisManager.sendAndAwait(MessageToEngine.from(order));
    }
}
