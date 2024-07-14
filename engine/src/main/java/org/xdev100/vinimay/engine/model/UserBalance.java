package org.xdev100.vinimay.engine.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UserBalance {
    private Map<String, BalanceInfo> balance;
    public UserBalance(Map<String, BalanceInfo> balance) {
        this.balance = balance;
    }

}
