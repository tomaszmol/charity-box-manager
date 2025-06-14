package com.charitybox.dto;

import com.charitybox.model.Currency;

import java.math.BigDecimal;

public class FundraisingEventDto {

    private String name;
    private BigDecimal accountBalance;
    private Currency accountCurrency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Currency getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(Currency accountCurrency) {
        this.accountCurrency = accountCurrency;
    }
}
