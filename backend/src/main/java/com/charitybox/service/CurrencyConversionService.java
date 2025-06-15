package com.charitybox.service;

import com.charitybox.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class CurrencyConversionService {
    private static final Map<Currency, BigDecimal> RATES_TO_PLN = Map.of(
            Currency.PLN, BigDecimal.ONE,
            Currency.EUR, new BigDecimal("4.5"),
            Currency.USD, new BigDecimal("4.0"),
            Currency.GBP, new BigDecimal("5.0")
    );

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == to) return amount;
        BigDecimal amountInPLN = amount.multiply(RATES_TO_PLN.get(from));
        return amountInPLN.divide(RATES_TO_PLN.get(to), 2, RoundingMode.HALF_UP);
    }
}
