package org.xdev100.vinimay.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {
    LIMIT, MARKET;

    @JsonCreator
    public static OrderType fromString(String key) {
        return key == null ? null : OrderType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
