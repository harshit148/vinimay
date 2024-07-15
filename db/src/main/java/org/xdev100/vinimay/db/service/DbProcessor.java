package org.xdev100.vinimay.db.service;

import java.time.Instant;
import java.time.ZoneId;

import org.xdev100.vinimay.db.model.SharePrice;
import org.xdev100.vinimay.engine.model.DatabaseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xdev100.vinimay.db.repository.SharePriceRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.xdev100.vinimay.engine.model.TradeAdded;

@Service
public class DbProcessor {

    @Autowired
    private SharePriceRepository sharePriceRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void startProcessing() {
        new Thread(() -> {
            while(true) {
                String response = redisTemplate.opsForList().rightPop("db_processor");
                if (response != null) {
                    processMessage(response);
                }
            }
        });
    }

    private void processMessage(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DatabaseMessage dbMessage = objectMapper.readValue(response, DatabaseMessage.class);
            if (dbMessage.getClass() == TradeAdded.class) {
                TradeAdded tradeAddedMessage = (TradeAdded)dbMessage;
                SharePrice sharePrice = new SharePrice();
                sharePrice.setTime(Instant.ofEpochMilli(tradeAddedMessage.getTimestamp()).atZone(ZoneId.systemDefault()));
                sharePrice.setPrice(tradeAddedMessage.getPrice());
                //sharePrice.setVolume(tradeAddedMessage.getVolume());
                sharePrice.setCurrencyCode(tradeAddedMessage.getMarket().split("_")[1]);
                sharePrice.setShareSymbol(tradeAddedMessage.getMarket().split("_")[0]);
                sharePriceRepository.save(sharePrice);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
