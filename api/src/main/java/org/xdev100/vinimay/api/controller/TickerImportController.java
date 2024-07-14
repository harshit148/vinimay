package org.xdev100.vinimay.api.controller;

import org.xdev100.vinimay.api.service.TickerDataImporter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/tickers/import")
public class TickerImportController {

    private TickerDataImporter tickerImportService;

    @Autowired
    public TickerImportController(TickerDataImporter tickerImportService) {
        this.tickerImportService = tickerImportService;
    }

    @PostMapping
    public ResponseEntity<String> importTicker() {
        tickerImportService.importTickersFromJson("/home/harshit/IdeaProjects/Vinimay/src/main/resources/tickers.json");
        return ResponseEntity.ok("Tickers imported from json file");
    }
}
