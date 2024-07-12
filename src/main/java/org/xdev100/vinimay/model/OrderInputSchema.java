package org.xdev100.vinimay.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Setter
@Getter
public class OrderInputSchema {

    @NotBlank(message = "Please specify the base asset")
    private String baseAsset;

    @NotBlank(message = "Please specify the quote asset")
    private String quoteAsset;

    @NotBlank(message = "Please specify the price of the asset")
    @Positive(message = "Invalid price. Please specify a positive price value")
    private double price;

    @NotBlank(message = "Please specify the quantity of the asset")
    @Positive(message = "Invalid quantity. Please specify a positive quantity value")
    private double quantity;

    @NotNull(message = "Please specify the side of order(buy|sell)")
    private OrderSide side;

    @NotNull(message = "Please specify the type of order(limit|market)")
    private OrderType type;

    private OrderKind kind;
}
