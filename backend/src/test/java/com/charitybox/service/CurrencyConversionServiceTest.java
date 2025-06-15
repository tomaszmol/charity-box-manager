package com.charitybox.service;

import com.charitybox.dto.NbpRateDto;
import com.charitybox.dto.NbpTableDto;
import com.charitybox.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyConversionServiceTest {

    private RestTemplate restTemplate;
    private CurrencyConversionService service;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        service = new CurrencyConversionService(restTemplate);
    }

    @Test
    void fetchRatesToPLN_shouldReturnRatesIncludingPLN() {
        // Arrange
        NbpRateDto eur = new NbpRateDto();
        eur.setCode("EUR");
        eur.setMid(new BigDecimal("4.50"));
        NbpTableDto table = new NbpTableDto();
        table.setRates(List.of(eur));
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class)))
                .thenReturn(new NbpTableDto[]{table});

        // Act
        Map<Currency, BigDecimal> rates = service.fetchRatesToPLN();

        // Assert
        assertEquals(BigDecimal.ONE, rates.get(Currency.PLN));
        assertEquals(new BigDecimal("4.50"), rates.get(Currency.EUR));
    }

    @Test
    void convert_shouldReturnSameAmountForSameCurrency() {
        // Act
        BigDecimal result = service.convert(new BigDecimal("100"), Currency.PLN, Currency.PLN);

        // Assert
        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void convert_shouldConvertBetweenCurrencies() {
        // Arrange
        NbpRateDto eur = new NbpRateDto();
        eur.setCode("EUR");
        eur.setMid(new BigDecimal("4.00"));
        NbpRateDto usd = new NbpRateDto();
        usd.setCode("USD");
        usd.setMid(new BigDecimal("5.00"));
        NbpTableDto table = new NbpTableDto();
        table.setRates(List.of(eur, usd));
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class)))
                .thenReturn(new NbpTableDto[]{table});

        // Act
        BigDecimal result = service.convert(new BigDecimal("2"), Currency.EUR, Currency.USD);

        // Assert
        // 2 EUR * 4.00 (EUR->PLN) = 8 PLN, 8 PLN / 5.00 (USD->PLN) = 1.60 USD
        assertEquals(new BigDecimal("1.60"), result);
    }

    @Test
    void convert_shouldThrowIfCurrencyNotSupported() {
        // Arrange
        NbpTableDto table = new NbpTableDto();
        table.setRates(List.of());
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class)))
                .thenReturn(new NbpTableDto[]{table});

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                service.convert(BigDecimal.ONE, Currency.EUR, Currency.USD));
    }

    @Test
    void fetchRatesToPLN_shouldHandleNullTables() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class))).thenReturn(null);

        // Act
        Map<Currency, BigDecimal> rates = service.fetchRatesToPLN();

        // Assert
        assertEquals(BigDecimal.ONE, rates.get(Currency.PLN));
        assertEquals(1, rates.size());
    }

    @Test
    void fetchRatesToPLN_shouldHandleEmptyTables() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class))).thenReturn(new NbpTableDto[0]);

        // Act
        Map<Currency, BigDecimal> rates = service.fetchRatesToPLN();

        // Assert
        assertEquals(BigDecimal.ONE, rates.get(Currency.PLN));
        assertEquals(1, rates.size());
    }

    @Test
    void convert_shouldThrowIfFromRateIsNull() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class))).thenReturn(new NbpTableDto[0]);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                service.convert(BigDecimal.ONE, Currency.EUR, Currency.PLN));
    }

    @Test
    void convert_shouldThrowIfToRateIsNull() {
        // Arrange
        NbpTableDto table = new NbpTableDto();
        table.setRates(java.util.List.of());
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class))).thenReturn(new NbpTableDto[]{table});

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                service.convert(BigDecimal.ONE, Currency.PLN, Currency.USD));
    }
}


