package com.charitybox.service;

import com.charitybox.config.FundraisingDefaultsProperties;
import com.charitybox.dto.FundraisingEventDto;
import com.charitybox.dto.FundraisingEventReportDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.model.Currency;
import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.CollectionBoxRepository;
import com.charitybox.repository.FundraisingEventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class FundraisingEventServiceTest {

    private FundraisingEventRepository fundraisingEventRepository;
    private CollectionBoxRepository collectionBoxRepository;
    private FundraisingEventService fundraisingEventService;
    private FundraisingDefaultsProperties defaults;

    @BeforeEach
    void setUp() {
        fundraisingEventRepository = mock(FundraisingEventRepository.class);
        collectionBoxRepository = mock(CollectionBoxRepository.class);
        defaults = mock(FundraisingDefaultsProperties.class);
        fundraisingEventService = new FundraisingEventService(
                fundraisingEventRepository,
                collectionBoxRepository,
                defaults);
    }

    @Test
    void createEvent_shouldCreateAndSaveEvent() {
        // Arrange
        FundraisingEventDto dto = new FundraisingEventDto();
        dto.setName("Charity");
        dto.setAccountBalance(BigDecimal.TEN);
        dto.setAccountCurrency(Currency.EUR.toString());

        FundraisingEvent savedEvent = new FundraisingEvent();
        savedEvent.setName("Charity");
        savedEvent.setAccountBalance(BigDecimal.TEN);
        savedEvent.setAccountCurrency(Currency.EUR);

        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenReturn(savedEvent);

        // Act
        FundraisingEvent result = fundraisingEventService.createEvent(dto);

        // Assert
        assertEquals("Charity", result.getName());
        assertEquals(BigDecimal.TEN, result.getAccountBalance());
        assertEquals(Currency.EUR, result.getAccountCurrency());
        verify(fundraisingEventRepository).save(any(FundraisingEvent.class));
    }

    @Test
    void assignCollectionBox_shouldAssignEmptyBoxToEvent() {
        // Arrange
        Long eventId = 1L;
        Long boxId = 2L;
        FundraisingEvent event = new FundraisingEvent();
        event.setId(eventId);

        CollectionBox box = new CollectionBox();
        box.setId(boxId);
        box.setCollectedAmounts(Map.of(Currency.PLN, BigDecimal.ZERO));

        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(box));

        // Act
        fundraisingEventService.assignCollectionBox(eventId, boxId);

        // Assert
        assertEquals(event, box.getFundraisingEvent());
        verify(collectionBoxRepository).save(box);
    }

    @Test
    void assignCollectionBox_shouldThrowIfBoxNotEmpty() {
        // Arrange
        Long eventId = 1L;
        Long boxId = 2L;
        FundraisingEvent event = new FundraisingEvent();
        event.setId(eventId);

        CollectionBox box = new CollectionBox();
        box.setId(boxId);
        box.setCollectedAmounts(Map.of(Currency.PLN, BigDecimal.ONE));

        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(box));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> fundraisingEventService.assignCollectionBox(eventId, boxId));
        verify(collectionBoxRepository, never()).save(any());
    }

    @Test
    void assignCollectionBox_shouldThrowIfEventNotFound() {
        // Arrange
        Long eventId = 1L;
        Long boxId = 2L;
        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> fundraisingEventService.assignCollectionBox(eventId, boxId));
    }

    @Test
    void assignCollectionBox_shouldThrowIfBoxNotFound() {
        // Arrange
        Long eventId = 1L;
        Long boxId = 2L;
        FundraisingEvent event = new FundraisingEvent();
        event.setId(eventId);

        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> fundraisingEventService.assignCollectionBox(eventId, boxId));
    }

    @Test
    void createEvent_shouldSetDefaultsWhenNullsProvided() {
        // Arrange
        FundraisingEventDto dto = new FundraisingEventDto();
        dto.setName("Charity");
        dto.setAccountBalance(null);
        dto.setAccountCurrency(null);


        when(defaults.getDefaultBalance()).thenReturn(BigDecimal.ZERO);
        when(defaults.getDefaultCurrency()).thenReturn(Currency.PLN);

        FundraisingEvent savedEvent = new FundraisingEvent();
        savedEvent.setName("Charity");
        savedEvent.setAccountBalance(defaults.getDefaultBalance());
        savedEvent.setAccountCurrency(defaults.getDefaultCurrency());

        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenReturn(savedEvent);

        // Act
        FundraisingEvent result = fundraisingEventService.createEvent(dto);

        // Assert
        assertEquals(BigDecimal.ZERO, result.getAccountBalance());
        assertEquals(Currency.PLN, result.getAccountCurrency());
    }

    @Test
    void createEvent_shouldSetDefaultCurrencyWhenOnlyBalanceProvided() {
        // Arrange
        FundraisingEventDto dto = new FundraisingEventDto();
        dto.setName("Charity");
        dto.setAccountBalance(BigDecimal.TEN);
        dto.setAccountCurrency(null);

        when(defaults.getDefaultCurrency()).thenReturn(Currency.PLN);

        FundraisingEvent savedEvent = new FundraisingEvent();
        savedEvent.setName("Charity");
        savedEvent.setAccountBalance(BigDecimal.TEN);
        savedEvent.setAccountCurrency(Currency.PLN);

        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenReturn(savedEvent);

        // Act
        FundraisingEvent result = fundraisingEventService.createEvent(dto);

        // Assert
        assertEquals(BigDecimal.TEN, result.getAccountBalance());
        assertEquals(Currency.PLN, result.getAccountCurrency());
    }

    @Test
    void createEvent_shouldSetDefaultBalanceWhenOnlyCurrencyProvided() {
        // Arrange
        FundraisingEventDto dto = new FundraisingEventDto();
        dto.setName("Charity");
        dto.setAccountBalance(null);
        dto.setAccountCurrency(Currency.EUR.toString());

        when(defaults.getDefaultBalance()).thenReturn(BigDecimal.ZERO);

        FundraisingEvent savedEvent = new FundraisingEvent();
        savedEvent.setName("Charity");
        savedEvent.setAccountBalance(BigDecimal.ZERO);
        savedEvent.setAccountCurrency(Currency.EUR);

        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenReturn(savedEvent);

        // Act
        FundraisingEvent result = fundraisingEventService.createEvent(dto);

        // Assert
        assertEquals(BigDecimal.ZERO, result.getAccountBalance());
        assertEquals(Currency.EUR, result.getAccountCurrency());
    }

    @Test
    void getFinancialReport_shouldReturnReportList() {
        // Arrange
        FundraisingEvent event = new FundraisingEvent();
        event.setName("Charity");
        event.setAccountBalance(BigDecimal.valueOf(100));
        event.setAccountCurrency(Currency.PLN);

        when(fundraisingEventRepository.findAll()).thenReturn(List.of(event));

        // Act
        List<FundraisingEventReportDto> report = fundraisingEventService.getFinancialReport();

        // Assert
        assertEquals(1, report.size());
        FundraisingEventReportDto dto = report.get(0);
        assertEquals("Charity", dto.getName());
        assertEquals(BigDecimal.valueOf(100), dto.getAccountBalance());
        assertEquals(Currency.PLN, dto.getAccountCurrency());
    }


}
