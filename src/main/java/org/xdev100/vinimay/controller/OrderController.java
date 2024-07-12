package org.xdev100.vinimay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.xdev100.vinimay.model.MessageFromOrderBook;
import org.xdev100.vinimay.model.OrderResponse;
import org.xdev100.vinimay.service.OrderService;
import org.xdev100.vinimay.model.OrderInputSchema;
import org.xdev100.vinimay.model.MessageToEngine;
import org.xdev100.vinimay.service.OrderServiceV2;

import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderServiceV2 orderServiceV2;

    @PostMapping("/v1/order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderInputSchema order){
        log.info("Hitting the order endpoint");
        OrderResponse orderResponse = orderService.processOrder(order);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/v2/order")
    public CompletableFuture<ResponseEntity<MessageFromOrderBook>> createOrder(@Valid @RequestBody MessageToEngine.CreateOrder order) throws JsonProcessingException {
        return orderServiceV2.createOrder(order).thenApply(ResponseEntity::ok);

    }

    @DeleteMapping("/v2/order")
    public CompletableFuture<ResponseEntity<MessageFromOrderBook>> cancelOrder(@Valid @RequestBody MessageToEngine.CancelOrder order) throws JsonProcessingException {
        return orderServiceV2.cancelOrder(order).thenApply(ResponseEntity::ok);
    }

    @GetMapping("/v2/order/open")
    public CompletableFuture<ResponseEntity<MessageFromOrderBook>> getOpenOrders(@RequestParam long userId, @RequestParam String market) throws JsonProcessingException {
        MessageToEngine.GetOpenOrder getOpenOrder = new MessageToEngine.GetOpenOrder();
        getOpenOrder.setMarket(market);
        getOpenOrder.setUserId(userId);
        return orderServiceV2.getOpenOrders(getOpenOrder).thenApply(ResponseEntity::ok);
    }


}

