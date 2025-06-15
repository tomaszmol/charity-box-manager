package com.charitybox.dto;

import com.charitybox.model.Currency;

import java.math.BigDecimal;


public class FundraisingEventReportDto {
    private String name;

    private BigDecimal accountBalance;

    private Currency accountCurrency;

    public FundraisingEventReportDto(String name, BigDecimal accountBalance, Currency accountCurrency) {
        this.name = name;
        this.accountBalance = accountBalance;
        this.accountCurrency = accountCurrency;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public Currency getAccountCurrency() {
        return accountCurrency;
    }

}
