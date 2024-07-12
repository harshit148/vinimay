package org.xdev100.vinimay.service;

import java.util.List;
import org.xdev100.vinimay.repository.TickerRepository;
import org.xdev100.vinimay.model.Ticker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class TickerService {
    private final TickerRepository tickerRepository;

    @Autowired
    public TickerService(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    public List<Ticker> getTickers() {
        return tickerRepository.findAll();
    }
    public Ticker getTickerBySymbol(String symbol) {
        return tickerRepository.findBySymbol(symbol);
    }
    public void saveTicker(Ticker ticker) {
        try {
            tickerRepository.save(ticker);
        }
        catch(DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Ticker with symbol '" + ticker.getSymbol() + "' already exists");
        }
    }
}
