package org.xdev100.vinimay.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderKind {
    IOC;

    @JsonCreator
    public static OrderSide fromString(String key) {
        return key == null ? null: OrderSide.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
