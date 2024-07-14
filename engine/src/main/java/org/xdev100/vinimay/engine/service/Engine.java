package org.xdev100.vinimay.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.xdev100.vinimay.api.model.*;
import org.xdev100.vinimay.engine.model.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


import com.google.gson.Gson;
import org.xdev100.vinimay.engine.model.OrderBook;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Service
@Slf4j
public class Engine {
    private List<OrderBook> orderBooks;
    private Map<String, UserBalance> balances;
    Supplier <Exception> orderBookException = () -> new Exception("No orderbook found");
    Supplier <Exception> orderException = () -> new Exception("No order found");
    private final String BASE_CURRENCY = "INR";
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private RedisPubSub redisPubSub;

    @Value("${WITH_SNAPSHOT:false}")
    private boolean withSnapShot;

    public Engine() {
        if(withSnapShot) {
            try {
                Resource resource = resourceLoader.getResource("classpath:static/snapshot.json");
                Path snapshotPath = resource.getFile().toPath();
                String snapshot = Files.readString(snapshotPath);
                SnapshotData snapshotData = new Gson().fromJson(snapshot, SnapshotData.class);
                orderBooks = snapshotData.getOrderbooks().stream()
                        .map(orderbook -> new OrderBook(orderbook.getBaseAsset(),
                                                        orderbook.getBids(),
                                                        orderbook.getAsks(),
                                                        orderbook.getLastTradeId(),
                                                        orderbook.getCurrentPrice())).collect(Collectors.toList());
                balances = new HashMap<>();
                snapshotData.getBalances().forEach(balanceEntry-> balances.put(balanceEntry.getKey(), balanceEntry.getValue()));
            }catch(IOException e) {
                log.error("Error loading snapshot file");
            }
        }
        else {
            orderBooks = Collections.singletonList(new OrderBook("TATA", Collections.emptyList(), Collections.emptyList(), 0L, 0.0));
            setBalances();
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        //scheduler.scheduleAtFixedRate(this::saveSnapshot, 0, 3, TimeUnit.SECONDS);
    }
    public void saveSnapshot() {
        SnapshotData snapshotData = new SnapshotData();
        snapshotData.setOrderbooks(orderBooks.stream().map(OrderBook::getSnapShot).toList());
        snapshotData.setBalances(balances.entrySet().stream().toList());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = resourceLoader.getResource("classpath:static/snapshot.json").getFile();
            objectMapper.writeValue(file, snapshotData);

        }catch (IOException e) {
            log.error("Error saving snapshot");
            e.printStackTrace();
        }
    }
    public void processMessage(MessageToEngine message, String clientId) {
        switch(message.getType()) {
            case "CREATE_ORDER":
                try {
                    MessageFromOrderBook orderResult = createOrder(message.getCreateOrder().getMarket(),
                                message.getCreateOrder().getPrice(),
                                message.getCreateOrder().getQuantity(),
                                message.getCreateOrder().getSide(),
                                message.getCreateOrder().getUserId());
                    redisPubSub.getInstance().sendToApi(clientId, orderResult);
                }
                catch (Exception e) {
                    log.error("Error while placing order");
                    e.printStackTrace();
                    redisPubSub.getInstance().sendToApi(clientId, MessageFromOrderBookFactory.createOrderCancelled("", 0, 0));
                }
                break;
            case "CANCEL_ORDER":
                try {
                    String orderId = message.getCancelOrder().getOrderId();
                    String cancelMarket = message.getCancelOrder().getMarket();
                    OrderBook cancelOrderBook = orderBooks.stream().filter(book -> book.ticker().equals(cancelMarket)).findAny().orElseThrow(orderBookException);
                    String quoteAsset = cancelMarket.split("_")[1];
                    Order cancelOrder = cancelOrderBook.getAsks().stream()
                                        .filter(ask -> ask.getOrderId().equals(orderId))
                                        .findAny()
                                        .orElse(cancelOrderBook.getBids().stream().filter(book -> book.getOrderId().equals(orderId)).findAny().orElseThrow(orderException));
                    if (cancelOrder.getSide() == OrderSide.BUY) {
                        double price = cancelOrderBook.cancelBid(cancelOrder);
                        double remainingQuantity = (cancelOrder.getQuantity() - cancelOrder.getFilled())*cancelOrder.getPrice();
                        double availableFunds =  balances.get(cancelOrder.getOrderId()).getBalance().get(BASE_CURRENCY).getAvailableFunds();
                        double lockedFunds =  balances.get(cancelOrder.getOrderId()).getBalance().get(BASE_CURRENCY).getLockedFunds();
                        balances.get(cancelOrder.getOrderId()).getBalance().get(BASE_CURRENCY).setAvailableFunds(availableFunds+remainingQuantity);
                        balances.get(cancelOrder.getOrderId()).getBalance().get(BASE_CURRENCY).setLockedFunds(lockedFunds-remainingQuantity);
                        sendUpdatedDepthAt(String.valueOf(price), cancelMarket);
                    }
                    else {
                        double price = cancelOrderBook.cancelAsk(cancelOrder);
                        double remainingQuantity = cancelOrder.getQuantity() - cancelOrder.getFilled();
                        double availableFunds =  balances.get(cancelOrder.getOrderId()).getBalance().get(quoteAsset).getAvailableFunds();
                        double lockedFunds =  balances.get(cancelOrder.getOrderId()).getBalance().get(quoteAsset).getLockedFunds();
                        balances.get(cancelOrder.getOrderId()).getBalance().get(quoteAsset).setAvailableFunds(availableFunds+remainingQuantity);
                        balances.get(cancelOrder.getOrderId()).getBalance().get(quoteAsset).setLockedFunds(lockedFunds-remainingQuantity);
                        sendUpdatedDepthAt(String.valueOf(price), cancelMarket);
                    }
                    redisPubSub.getInstance().sendToApi(clientId, MessageFromOrderBookFactory.createOrderCancelled(orderId, 0, 0));
                } catch (Exception e) {
                    log.error("Error while cancelling order");
                    e.printStackTrace();
                }
                break;
            case "GET_OPEN_ORDERS":
                String requestedMarket = message.getGetOpenOrder().getMarket();
                try {
                    OrderBook openOrderBook = orderBooks.stream()
                                                        .filter(book -> book.ticker().equals(requestedMarket))
                                                        .findAny()
                                                        .orElseThrow(orderBookException);
                    MessageFromOrderBook openOrders = openOrderBook.getOpenOrders(message.getGetOpenOrder().getUserId());
                    redisPubSub.getInstance().sendToApi(clientId, openOrders);
                } catch (Exception e) {
                    log.error("Error while getting open orders");
                    e.printStackTrace();
                }
                break;
            case "ON_RAMP":
                String userId = message.getOnRamp().getUserId();
                double amount = message.getOnRamp().getAmount();
                onRamp(userId, amount);
                break;
            case "GET_DEPTH":
                String depthMarket = message.getGetDepth().getMarket();
                try {
                    OrderBook depthOrderBook = orderBooks.stream()
                            .filter(book -> book.ticker().equals(depthMarket))
                            .findAny()
                            .orElseThrow(orderBookException);
                    redisPubSub.getInstance().sendToApi(clientId, depthOrderBook.getDepth());
                } catch (Exception e) {
                    log.error("Error while getting depth of the market: " + depthMarket);
                    redisPubSub.getInstance().sendToApi(clientId, new DepthResponse());
                    e.printStackTrace();
                }
        }
    }
    public void addOrderBook(OrderBook orderBook) {
        this.orderBooks.add(orderBook);
    }

    public void checkAndLockFunds(String baseAsset, String quoteAsset, OrderSide side, String userId, double price, double quantity) throws Exception {
        if (side == OrderSide.BUY) {
            double availableFunds = balances.get(userId).getBalance().get(quoteAsset).getAvailableFunds();
            double lockedFunds = balances.get(userId).getBalance().get(quoteAsset).getLockedFunds();
            if (availableFunds < quantity*price) {
                throw new Exception("Insufficient funds(" + quoteAsset + "): " + availableFunds);
            }
            this.balances.get(userId).getBalance().get(quoteAsset).setAvailableFunds(availableFunds-(quantity*price));
            this.balances.get(userId).getBalance().get(quoteAsset).setLockedFunds(lockedFunds+(quantity*price));
        }
        else {
            double availableFunds = balances.get(userId).getBalance().get(baseAsset).getAvailableFunds();
            double lockedFunds = balances.get(userId).getBalance().get(baseAsset).getLockedFunds();
            if (availableFunds < quantity) {
                throw new Exception("Insufficient shares(" + baseAsset + "): " + availableFunds);
            }
            this.balances.get(userId).getBalance().get(baseAsset).setAvailableFunds(availableFunds-quantity);
            this.balances.get(userId).getBalance().get(baseAsset).setLockedFunds(lockedFunds+quantity);
        }
    }

    public void sendUpdatedDepthAt(String price, String market) throws Exception {
        OrderBook updateOrderBook = orderBooks.stream().filter(book -> book.ticker().equals(market)).findAny().orElseThrow(orderBookException);
        DepthResponse depthResponse = updateOrderBook.getDepth();
        List <Pair<String, String>> updateBids = new ArrayList<>(depthResponse.getBids().stream()
                .filter(bid -> bid[0].equals(price))
                .map(bid -> Pair.of(bid[0], bid[1]))
                .toList());
        List <Pair<String, String>> updateAsks = new ArrayList<>(depthResponse.getAsks().stream()
                .filter(ask -> ask[0].equals(price))
                .map(ask -> Pair.of(ask[0], ask[1]))
                .toList());
        if (updateAsks.isEmpty()) {
            updateAsks.add(Pair.of(price, "0"));
        }
        if (updateBids.isEmpty()) {
            updateBids.add(Pair.of(price, "0"));
        }
        WebSocketMessage depthMessage = new DepthUpdateMessage(updateAsks, updateBids);
        depthMessage.setE("depth");
        redisPubSub.getInstance().publishMessage("depth@" + market, depthMessage);
    }
    public MessageFromOrderBook createOrder(String market, double price, double quantity, OrderSide side, String userId) throws Exception {
        Supplier <Exception> orderBookException = () -> new Exception("No orderbook found");
        OrderBook orderBook = this.orderBooks.stream().filter(book -> book.ticker().equals(market)).findAny().orElseThrow(orderBookException);
        String baseAsset = market.split("_")[0];
        String quoteAsset = market.split("_")[1];

        checkAndLockFunds(baseAsset, quoteAsset, side, userId, price, quantity);
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(price, quantity, side, orderId, 0, userId);
        FillOrderResult orderResult = orderBook.addOrder(order);
        updateBalance(userId, baseAsset, quoteAsset, side, orderResult.getFills(), orderResult.getExecutedQty());
        createDbTrades(orderResult.getFills(), market, userId);
        updateDbOrders(order, orderResult.getExecutedQty(), orderResult.getFills(), market);
        publishWsDepthUpdates(orderResult.getFills(), price, side, market);
        publishWsTrades(orderResult.getFills(), userId, market);
        return MessageFromOrderBookFactory.createOrderPlaced(orderResult.getOrderId(), orderResult.getExecutedQty(), orderResult.getFills());

    }
    public void createDbTrades(List<Fill> fills, String market, String userId) {
        fills.forEach(fill -> {
            DatabaseMessage tradeAddedMessage = new TradeAdded(String.valueOf(fill.getTradeId()),
                                                                fill.getOtherUserId().equals(userId),
                                                                fill.getQty()*fill.getPrice(),
                                                                Instant.now().toEpochMilli());
            tradeAddedMessage.setMarket(market);
            tradeAddedMessage.setPrice(fill.getPrice());
            tradeAddedMessage.setQuantity(fill.getQty());
            redisPubSub.getInstance().pushMessage(tradeAddedMessage);
        });
    }
    public void updateDbOrders(Order order, double executedQty, List<Fill> fills, String market) {
        DatabaseMessage orderUpdate = new OrderUpdate(order.getOrderId(), executedQty, order.getSide());
        orderUpdate.setQuantity(order.getQuantity());
        orderUpdate.setPrice(order.getPrice());
        orderUpdate.setMarket(market);
        redisPubSub.getInstance().pushMessage(orderUpdate);
        fills.forEach(fill -> {
           OrderUpdate filledOrder = new OrderUpdate();
           filledOrder.setOrderId(fill.getMarketOrderId());
           filledOrder.setExecutedQty(fill.getQty());
           redisPubSub.getInstance().pushMessage(filledOrder);
        });
    }
    public void publishWsDepthUpdates(List <Fill> fills, double price, OrderSide side, String market) throws Exception {
        OrderBook orderBook = orderBooks.stream()
                                .filter(book -> book.ticker().equals(market))
                                .findAny().orElseThrow(orderBookException);
        DepthResponse depth = orderBook.getDepth();
        if (side == OrderSide.BUY) {
            List <Pair<String, String>> updateBids = new ArrayList<>(depth.getBids().stream()
                    .filter(bid -> Double.parseDouble(bid[0]) == price)
                    .map(bid -> Pair.of(bid[0], bid[1]))
                    .toList());
            List <Pair<String, String>> updateAsks = new ArrayList<>(depth.getAsks().stream()
                    .filter(ask -> fills.stream().map(Fill::getPrice).map(Object::toString).toList().contains(ask[0]))
                    .map(ask -> Pair.of(ask[0], ask[1]))
                    .toList());
            log.info("Publish ws depth updates");
            WebSocketMessage depthMessage = new DepthUpdateMessage(updateAsks, updateBids);
            depthMessage.setE("depth");
            redisPubSub.getInstance().publishMessage("depth@" + market, depthMessage);
        }
        if (side == OrderSide.SELL) {
            List <Pair<String, String>> updateAsks = new ArrayList<>(depth.getBids().stream()
                    .filter(ask -> Double.parseDouble(ask[0]) == price)
                    .map(ask -> Pair.of(ask[0], ask[1]))
                    .toList());
            List <Pair<String, String>> updateBids = new ArrayList<>(depth.getAsks().stream()
                    .filter(bid -> fills.stream().map(Fill::getPrice).map(Object::toString).toList().contains(bid[0]))
                    .map(bid -> Pair.of(bid[0], bid[1]))
                    .toList());
            log.info("Publish ws depth updates");
            WebSocketMessage depthMessage = new DepthUpdateMessage(updateAsks, updateBids);
            depthMessage.setE("depth");
            redisPubSub.getInstance().publishMessage("depth@" + market, depthMessage);
        }
    }
    public void publishWsTrades(List<Fill> fills, String userId, String market) {
        fills.forEach(fill -> {
            WebSocketMessage tradeUpdate = new TradeAddedMessage(fill.getTradeId(),
                                                                fill.getOtherUserId().equals(userId),
                                                                String.valueOf(fill.getPrice()),
                                                                String.valueOf(fill.getQty()),
                                                                market);
            tradeUpdate.setE("trade");
            redisPubSub.getInstance().publishMessage("trades@"+market, tradeUpdate);
        });
    }
    public void updateBalance(String userId, String baseAsset, String quoteAsset, OrderSide side, List<Fill> fills, double executedQty) {
        if (side == OrderSide.BUY) {
            fills.forEach(fill -> {
                double availableQuoteAsset = balances.get(fill.getOtherUserId()).getBalance().get(quoteAsset).getAvailableFunds();
                double lockedQuoteAsset = balances.get(userId).getBalance().get(quoteAsset).getLockedFunds();
                double availableBaseAsset = balances.get(userId).getBalance().get(baseAsset).getAvailableFunds();
                double lockedBaseAsset = balances.get(fill.getOtherUserId()).getBalance().get(baseAsset).getLockedFunds();
                balances.get(fill.getOtherUserId()).getBalance().get(quoteAsset).setAvailableFunds(availableQuoteAsset+(fill.getQty()*fill.getPrice()));
                balances.get(userId).getBalance().get(quoteAsset).setLockedFunds(lockedQuoteAsset-(fill.getQty()*fill.getPrice()));
                balances.get(fill.getOtherUserId()).getBalance().get(baseAsset).setLockedFunds(lockedBaseAsset-fill.getQty());
                balances.get(userId).getBalance().get(baseAsset).setAvailableFunds(availableBaseAsset+fill.getQty());
            });
        }
        else {
            fills.forEach(fill -> {
                double availableQuoteAsset = balances.get(userId).getBalance().get(quoteAsset).getAvailableFunds();
                double lockedQuoteAsset = balances.get(fill.getOtherUserId()).getBalance().get(quoteAsset).getLockedFunds();
                double availableBaseAsset = balances.get(fill.getOtherUserId()).getBalance().get(baseAsset).getAvailableFunds();
                double lockedBaseAsset = balances.get(userId).getBalance().get(baseAsset).getLockedFunds();
                balances.get(userId).getBalance().get(quoteAsset).setAvailableFunds(availableQuoteAsset+(fill.getQty()*fill.getPrice()));
                balances.get(fill.getOtherUserId()).getBalance().get(quoteAsset).setLockedFunds(lockedQuoteAsset-(fill.getQty()*fill.getPrice()));
                balances.get(userId).getBalance().get(baseAsset).setLockedFunds(lockedBaseAsset-fill.getQty());
                balances.get(fill.getOtherUserId()).getBalance().get(baseAsset).setAvailableFunds(availableBaseAsset+fill.getQty());
            });
        }
    }
    public void onRamp(String userId, double amount) {
        if (!balances.containsKey(userId)) {
            balances.get(userId).getBalance().get(BASE_CURRENCY).setAvailableFunds(amount);
            balances.get(userId).getBalance().get(BASE_CURRENCY).setLockedFunds(0);
        }
        else {
            double availableFunds = balances.get(userId).getBalance().get(BASE_CURRENCY).getAvailableFunds();
            balances.get(userId).getBalance().get(BASE_CURRENCY).setAvailableFunds(availableFunds+amount);
        }
    }
    public void setBalances() {
        Map<String, BalanceInfo> initialBalances = new HashMap<>();
        initialBalances.put(BASE_CURRENCY, new BalanceInfo(10000000, 0));
        initialBalances.put("TATA", new BalanceInfo(10000000, 0));

        UserBalance userBalance1 = new UserBalance(initialBalances);
        UserBalance userBalance2 = new UserBalance(initialBalances);
        UserBalance userBalance5 = new UserBalance(initialBalances);
        balances = new HashMap<>();
        balances.put("1", userBalance1);
        balances.put("2", userBalance2);
        balances.put("5", userBalance5);
    }
}
