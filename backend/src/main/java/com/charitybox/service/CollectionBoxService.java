package com.charitybox.service;

import com.charitybox.dto.CollectionBoxDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.model.Currency;
import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.CollectionBoxRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectionBoxService {
    private final CollectionBoxRepository collectionBoxRepository;
    private final CurrencyConversionService currencyConversionService;

    public CollectionBoxService (CollectionBoxRepository collectionBoxRepository, CurrencyConversionService currencyConversionService){
        this.collectionBoxRepository = collectionBoxRepository;
        this.currencyConversionService = currencyConversionService;
    }

    public CollectionBox createBox(CollectionBox box){
        return collectionBoxRepository.save(box);
    }

    public List<CollectionBoxDto> listBoxes() {
        return collectionBoxRepository.findAll().stream()
                .map(box -> new CollectionBoxDto(
                        box.getId(),
                        box.getFundraisingEvent() != null,
                        box.getCollectedAmounts().values().stream().allMatch(amount -> amount.compareTo(BigDecimal.ZERO) == 0)
                ))
                .collect(Collectors.toList());
    }

    public void deleteBox(Long id) {
        CollectionBox box = collectionBoxRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CollectionBox not found: " + id));
        // Empty box first
        for (Currency currency : box.getCollectedAmounts().keySet()) {
            box.getCollectedAmounts().put(currency, BigDecimal.ZERO);
        }
        collectionBoxRepository.save(box);
        collectionBoxRepository.deleteById(id);
    }

    public void addMoney(Long boxId, String currencyStr, BigDecimal amount) {
        Currency currency;
        try {
            currency = Currency.valueOf(currencyStr);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Unsupported currency: " + currencyStr);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be higher or equal to 0");
        }
        CollectionBox box = collectionBoxRepository.findById(boxId)
                .orElseThrow(() -> new EntityNotFoundException("Box not found: " + boxId));
        if (box.getFundraisingEvent() == null) {
            throw new IllegalStateException("Box is not assigned to any fundraising event. Adding money will prevent assigning box: "+ boxId + " to an event.");
        }
        box.getCollectedAmounts().merge(currency, amount, BigDecimal::add);
        collectionBoxRepository.save(box);
    }

    public void emptyBox(Long boxId) {
        CollectionBox box = collectionBoxRepository.findById(boxId)
                .orElseThrow(() -> new EntityNotFoundException("Box not found: " + boxId));
        if (box.getFundraisingEvent() == null) {
            throw new IllegalStateException("Box is not assigned to any fundraising event.");
        }
        FundraisingEvent event = box.getFundraisingEvent();
        Currency eventCurrency = event.getAccountCurrency();

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Currency, BigDecimal> entry : box.getCollectedAmounts().entrySet()) {
            BigDecimal converted = currencyConversionService.convert(entry.getValue(), entry.getKey(), eventCurrency);
            total = total.add(converted);
            box.getCollectedAmounts().put(entry.getKey(), BigDecimal.ZERO);
        }
        event.setAccountBalance(event.getAccountBalance().add(total));
        collectionBoxRepository.save(box);
    }
}
