package org.xdev100.vinimay.controller;

import java.util.List;


import org.springframework.web.bind.annotation.*;
import org.xdev100.vinimay.model.Ticker;
import org.xdev100.vinimay.service.TickerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/tickers")
public class TickerController {

    @Autowired
    private TickerService tickerService;

    @GetMapping
    public List<Ticker> getAllTicker() {
        return tickerService.getTickers();
    }

    @GetMapping("/{symbol}")
    public Ticker getTicker(@PathVariable String symbol) {
        return tickerService.getTickerBySymbol(symbol);
    }

    @PostMapping
    public ResponseEntity<String> saveTicker( @Valid @RequestBody Ticker ticker) {
        try {
            tickerService.saveTicker(ticker);
            return ResponseEntity.ok("Ticker saved successfully");
        }
        catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
