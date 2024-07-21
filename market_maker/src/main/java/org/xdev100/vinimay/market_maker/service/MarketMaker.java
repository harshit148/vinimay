package org.xdev100.vinimay.market_maker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xdev100.vinimay.api.model.MessageToEngine;
import org.xdev100.vinimay.api.model.Order;
import org.xdev100.vinimay.api.model.OrderSide;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

@Service
@Slf4j
public class MarketMaker {
    @Autowired
    private RestTemplate restTemplate; // Assuming you have configured RestTemplate in your AppConfig

    @Value("${market_maker.base_url}") // Configure this in your application.properties
    private String baseUrl;

    @Value("${market_maker.total_bids}")
    private int totalBids;

    @Value("${market_maker.total_asks}")
    private int totalAsks;

    @Value("${market_maker.market}")
    private String market;

    @Value("${market_maker.user_id}")
    private String userId;

    private final Random random = new Random();

    @Scheduled(fixedDelay = 10000) // Run every 10 seconds, adjust as needed
    public void executeMarketMakerStrategy() {
        try {
            log.info("Market making");
            double price = 1000 + random.nextDouble() * 10;
            ResponseEntity<Order[]> openOrdersResponse = restTemplate.exchange(
                    baseUrl + "/api/v2/order/open?userId={userId}&market={market}",
                    HttpMethod.GET, null, Order[].class, userId, market);

            Order[] openOrders = openOrdersResponse.getBody();
            if (openOrders != null) {
                int totalBids = countOrders(openOrders, OrderSide.BUY);
                int totalAsks = countOrders(openOrders, OrderSide.SELL);

                int cancelledBids = cancelBidsMoreThan(openOrders, price);
                int cancelledAsks = cancelAsksLessThan(openOrders, price);

                int bidsToAdd = this.totalBids - totalBids - cancelledBids;
                int asksToAdd = this.totalAsks - totalAsks - cancelledAsks;

                while (bidsToAdd > 0 || asksToAdd > 0) {
                    if (bidsToAdd > 0) {
                        placeOrder(price - random.nextDouble() * 1, 1, OrderSide.BUY);
                        bidsToAdd--;
                    }
                    if (asksToAdd > 0) {
                        placeOrder(price + random.nextDouble() * 1, 1, OrderSide.SELL);
                        asksToAdd--;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int countOrders(Order[] orders, OrderSide side) {
        return (int) Arrays.stream(orders)
                .filter(order -> order.getSide() == side)
                .count();
    }

    private int cancelBidsMoreThan(Order[] orders, double price) {
        int cancelled = 0;
        for (Order order : orders) {
            if (order.getSide() == OrderSide.BUY  && (order.getPrice() > price || random.nextDouble() < 0.1)) {
                restTemplate.exchange(baseUrl + "/api/v2/order",
                        HttpMethod.DELETE, new HttpEntity<>(order), Void.class);
                cancelled++;
            }
        }
        return cancelled;
    }

    private int cancelAsksLessThan(Order[] orders, double price) {
        int cancelled = 0;
        for (Order order : orders) {
            if (order.getSide() == OrderSide.SELL && (order.getPrice() < price || random.nextDouble() < 0.5)) {
                restTemplate.exchange(baseUrl + "/api/v2/order",
                        HttpMethod.DELETE, new HttpEntity<>(order), Void.class);
                cancelled++;
            }
        }
        return cancelled;
    }

    private void placeOrder(double price, double quantity, OrderSide side) {
        MessageToEngine.CreateOrder order = new MessageToEngine.CreateOrder();
        order.setMarket(market);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setSide(side);
        order.setUserId(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        restTemplate.exchange(baseUrl + "/api/v2/order",
                HttpMethod.POST, new HttpEntity<>(order, headers), Void.class);
    }
}

