package org.xdev100.vinimay.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final String BASE_ASSET = "BTC";
    private static final String QUOTE_ASSET = "USD";
    private int globalTradeId = 0;
    //private OrderBook orderBook = OrderBook.getInstance();

    public void processOrder() {

    }
}
