package org.xdev100.vinimay.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.xdev100.vinimay.model.Ticker;
import org.xdev100.vinimay.model.TickerJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TickerDataImporter {
    private TickerService tickerService;

    @Autowired
    public TickerDataImporter(TickerService tickerService) {
        this.tickerService = tickerService;
    }
    public void importTickersFromJson(String jsonFilePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TickerJson[] tickersJson = objectMapper.readValue(new File(jsonFilePath), TickerJson[].class);
            Arrays.stream(tickersJson).map(this::convertToTicker).forEach(tickerService::saveTicker);
            System.out.println("Tickers imported successfully");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Ticker convertToTicker(TickerJson tickerJson) {
        Ticker ticker = new Ticker();
        ticker.setSymbol(tickerJson.getSymbol());
        ticker.setLow(Double.parseDouble(tickerJson.getLow()));
        ticker.setHigh(Double.parseDouble(tickerJson.getHigh()));
        ticker.setFirstPrice(Double.parseDouble(tickerJson.getFirstPrice()));
        ticker.setLastPrice(Double.parseDouble(tickerJson.getLastPrice()));
        ticker.setPriceChange(Double.parseDouble(tickerJson.getPriceChange()));
        ticker.setPriceChangePercent(Double.parseDouble(tickerJson.getPriceChangePercent()));
        ticker.setTrades(Long.parseLong(tickerJson.getTrades()));
        ticker.setVolume(Double.parseDouble(tickerJson.getVolume()));
        ticker.setQuoteVolume(Double.parseDouble(tickerJson.getQuoteVolume()));
        return ticker;

    }
}
