package org.xdev100.vinimay.engine.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SnapshotData {
    private List<OrderBook> orderbooks;
    private List<Map.Entry<String, UserBalance>> balances;
}
