package org.xdev100.vinimay.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderSide {
    BUY, SELL;

    @JsonCreator
    public static OrderSide fromString(String key) {
        return key == null ? null: OrderSide.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

}
