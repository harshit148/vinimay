package org.xdev100.vinimay.engine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceInfo {
    private double availableFunds;
    private double lockedFunds;

    public BalanceInfo(double availableFunds, double lockedFunds) {
        this.availableFunds = availableFunds;
        this.lockedFunds = lockedFunds;
    }
}
