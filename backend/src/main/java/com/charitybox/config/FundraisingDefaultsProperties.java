package com.charitybox.config;

import com.charitybox.model.Currency;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "fundraising")
public class FundraisingDefaultsProperties {
    private BigDecimal defaultBalance = BigDecimal.ZERO;
    private Currency defaultCurrency = Currency.PLN;

    public BigDecimal getDefaultBalance() {
        return defaultBalance;
    }

    public void setDefaultBalance(BigDecimal defaultBalance) {
        this.defaultBalance = defaultBalance;
    }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}