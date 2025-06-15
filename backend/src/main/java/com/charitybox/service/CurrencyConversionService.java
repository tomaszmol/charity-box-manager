package com.charitybox.service;

import com.charitybox.dto.NbpTableDto;
import com.charitybox.model.Currency;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyConversionService {
    private final RestTemplate restTemplate;
    private static final String NBP_URL = "https://api.nbp.pl/api/exchangerates/tables/A?format=json";

    public CurrencyConversionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<Currency, BigDecimal> fetchRatesToPLN() {
        NbpTableDto[] tables = restTemplate.getForObject(NBP_URL, NbpTableDto[].class);
        Map<Currency, BigDecimal> rates = new HashMap<>();
        rates.put(Currency.PLN, BigDecimal.ONE);

        if (tables != null && tables.length > 0) {
            for (var rate : tables[0].getRates()) {
                try {
                    Currency currency = Currency.valueOf(rate.getCode());
                    rates.put(currency, rate.getMid());
                } catch (IllegalArgumentException ignored) {
                    // skip unsupported currencies
                }
            }
        }
        return rates;
    }

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        Map<Currency, BigDecimal> rates = fetchRatesToPLN();
        if (from == to) return amount;
        BigDecimal fromRate = rates.getOrDefault(from, null);
        BigDecimal toRate = rates.getOrDefault(to, null);
        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("No currency exchange rate: " + from + " or " + to);
        }
        BigDecimal amountInPLN = amount.multiply(fromRate);
        return amountInPLN.divide(toRate, 2, RoundingMode.HALF_UP);
    }
}
