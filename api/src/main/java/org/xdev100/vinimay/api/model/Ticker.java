package org.xdev100.vinimay.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "Ticker")
public class Ticker {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @Id
    @Column(name="symbol")
    private String symbol;

    @Column(name="first_price")
    private double firstPrice;

    @Column(name="high")
    private double high;

    @Column(name="low")
    private double low;

    @Column(name="last_price")
    private double lastPrice;

    @Column(name="price_change")
    private double priceChange;

    @Column(name="price_change_percent")
    private double priceChangePercent;

    @Column(name="quote_volume")
    private double quoteVolume;

    @Column(name="trades")
    private long trades;

    @Column(name="volume")
    private double volume;

}
