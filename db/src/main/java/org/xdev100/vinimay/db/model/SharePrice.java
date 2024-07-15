package org.xdev100.vinimay.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name= "share_price")
@Getter
@Setter
public class SharePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time", nullable = false)
    private ZonedDateTime time;

    //@Column(name = "volume")
    //private Double volume;

    @Column(name ="price", nullable = false)
    private Double price;

    @Column(name="currency_code", length = 10)
    private String currencyCode;

    @Column(name="share_symbol", nullable = false)
    private String shareSymbol;
}
