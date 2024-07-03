package org.xdev100.vinimay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import org.xdev100.vinimay.model.OrderBook;
import org.xdev100.vinimay.model.OrderResponse;
import org.xdev100.vinimay.service.OrderService;
import org.xdev100.vinimay.model.OrderInputSchema;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderInputSchema order){
        log.info("Hitting the order endpoint");
        OrderResponse orderResponse = orderService.processOrder(order);
        return ResponseEntity.ok(orderResponse);
    }

}
