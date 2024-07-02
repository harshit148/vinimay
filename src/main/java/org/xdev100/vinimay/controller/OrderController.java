package org.xdev100.vinimay.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import org.xdev100.vinimay.service.OrderService;
import org.xdev100.vinimay.model.OrderInputSchema;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@Valid @RequestBody OrderInputSchema order){
        orderService.processOrder();
        return ResponseEntity.ok("Test");
    }

}
