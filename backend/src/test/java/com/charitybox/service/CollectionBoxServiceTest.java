package com.charitybox.service;

import com.charitybox.dto.CollectionBoxDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.model.Currency;
import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.CollectionBoxRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class CollectionBoxServiceTest {

    private CollectionBoxRepository collectionBoxRepository;
    private CurrencyConversionService currencyConversionService;
    private CollectionBoxService collectionBoxService;

    @BeforeEach
    void setUp() {
        collectionBoxRepository = mock(CollectionBoxRepository.class);
        currencyConversionService = mock(CurrencyConversionService.class);
        collectionBoxService = new CollectionBoxService(collectionBoxRepository, currencyConversionService);
    }

    @Test
    void createBox_shouldSaveAndReturnBox() {
        // Arrange
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.save(box)).thenReturn(box);

        // Act
        CollectionBox result = collectionBoxService.createBox(box);

        // Assert
        assertEquals(box, result);
        verify(collectionBoxRepository).save(box);
    }

    @Test
    void listBoxes_shouldReturnDtos() {
        // Arrange
        CollectionBox box1 = new CollectionBox();
        box1.setId(1L);
        box1.setFundraisingEvent(new FundraisingEvent());
        CollectionBox box2 = new CollectionBox();
        box2.setId(2L);

        when(collectionBoxRepository.findAll()).thenReturn(List.of(box1, box2));

        // Act
        List<CollectionBoxDto> dtos = collectionBoxService.listBoxes();

        // Assert
        assertEquals(2, dtos.size());
        assertTrue(dtos.get(0).isAssigned());
        assertFalse(dtos.get(1).isAssigned());
    }

    @Test
    void deleteBox_shouldEmptyAndDeleteBox() {
        // Arrange
        CollectionBox box = new CollectionBox();
        box.setId(1L);
        box.getCollectedAmounts().put(Currency.PLN, new BigDecimal("100"));
        when(collectionBoxRepository.findById(1L)).thenReturn(Optional.of(box));

        // Act
        collectionBoxService.deleteBox(1L);

        // Assert
        assertEquals(BigDecimal.ZERO, box.getCollectedAmounts().get(Currency.PLN));
        verify(collectionBoxRepository).save(box);
        verify(collectionBoxRepository).deleteById(1L);
    }

    @Test
    void addMoney_shouldAddAmount() {
        // Arrange
        CollectionBox box = new CollectionBox();
        box.setId(1L);
        FundraisingEvent event = new FundraisingEvent();
        box.setFundraisingEvent(event);
        when(collectionBoxRepository.findById(1L)).thenReturn(Optional.of(box));

        // Act
        collectionBoxService.addMoney(1L, Currency.PLN.toString(), new BigDecimal("50"));

        // Assert
        assertEquals(new BigDecimal("50"), box.getCollectedAmounts().get(Currency.PLN));
        verify(collectionBoxRepository).save(box);
    }

    @Test
    void addMoney_shouldThrowIfAmountNegative() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                collectionBoxService.addMoney(1L, Currency.PLN.toString(), new BigDecimal("-1")));
    }

    @Test
    void addMoney_shouldThrowIfBoxNotFound() {
        // Arrange
        when(collectionBoxRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                collectionBoxService.addMoney(1L, Currency.PLN.toString(), BigDecimal.ONE));
    }

    @Test
    void addMoney_shouldThrowIfBoxNotAssigned() {
        // Arrange
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.findById(1L)).thenReturn(Optional.of(box));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                collectionBoxService.addMoney(1L, Currency.PLN.toString(), BigDecimal.ONE));
    }

    @Test
    void emptyBox_shouldTransferAndZeroAmounts() {
        // Arrange
        CollectionBox box = new CollectionBox();
        box.setId(1L);
        FundraisingEvent event = new FundraisingEvent();
        event.setAccountCurrency(Currency.PLN);
        event.setAccountBalance(BigDecimal.ZERO);
        box.setFundraisingEvent(event);

        // Ustaw wartości dla wszystkich walut
        for (Currency currency : Currency.values()) {
            box.getCollectedAmounts().put(currency, BigDecimal.ZERO);
        }
        box.getCollectedAmounts().put(Currency.PLN, new BigDecimal("10"));

        when(collectionBoxRepository.findById(1L)).thenReturn(Optional.of(box));
        // Mockuj konwersję dla wszystkich walut
        for (Currency currency : Currency.values()) {
            if (currency == Currency.PLN) {
                when(currencyConversionService.convert(new BigDecimal("10"), Currency.PLN, Currency.PLN)).thenReturn(new BigDecimal("10"));
            } else {
                when(currencyConversionService.convert(BigDecimal.ZERO, currency, Currency.PLN)).thenReturn(BigDecimal.ZERO);
            }
        }

        // Act
        collectionBoxService.emptyBox(1L);

        // Assert
        assertEquals(BigDecimal.ZERO, box.getCollectedAmounts().get(Currency.PLN));
        assertEquals(new BigDecimal("10"), event.getAccountBalance());
        verify(collectionBoxRepository).save(box);
    }

    @Test
    void emptyBox_shouldThrowIfBoxNotFound() {
        // Arrange
        when(collectionBoxRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> collectionBoxService.emptyBox(1L));
    }

    @Test
    void emptyBox_shouldThrowIfBoxNotAssigned() {
        // Arrange
        CollectionBox box = new CollectionBox();
        when(collectionBoxRepository.findById(1L)).thenReturn(Optional.of(box));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> collectionBoxService.emptyBox(1L));
    }

    @Test
    void addMoney_shouldThrowIfAmountIsNull() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                collectionBoxService.addMoney(1L, Currency.PLN.toString(), null));
    }
}
