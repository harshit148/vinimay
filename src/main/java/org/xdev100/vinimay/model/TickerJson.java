package org.xdev100.vinimay.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TickerJson {
    private String symbol;
    private String firstPrice;
    private String high;
    private String low;
    private String lastPrice;
    private String priceChange;
    private String priceChangePercent;
    private String quoteVolume;
    private String trades;
    private String volume;
}
