package com.charitybox.dto;

import com.charitybox.model.Currency;

import java.math.BigDecimal;

public class AddMoneyRequest {
    private String currency;
    private BigDecimal amount;

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
