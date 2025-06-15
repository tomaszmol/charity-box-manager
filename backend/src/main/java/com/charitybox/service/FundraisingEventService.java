package com.charitybox.service;

import com.charitybox.dto.FundraisingEventDto;
import com.charitybox.dto.FundraisingEventReportDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.model.Currency;
import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.CollectionBoxRepository;
import com.charitybox.repository.FundraisingEventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundraisingEventService {
    private final FundraisingEventRepository fundraisingEventRepository;
    private final CollectionBoxRepository collectionBoxRepository;

    public FundraisingEventService(FundraisingEventRepository fundraisingEventRepository, CollectionBoxRepository collectionBoxRepository){
        this.fundraisingEventRepository = fundraisingEventRepository;
        this.collectionBoxRepository = collectionBoxRepository;

    }

    public FundraisingEvent createEvent(FundraisingEventDto dto){
        FundraisingEvent event = new FundraisingEvent();
        event.setName(dto.getName());
        event.setAccountBalance(dto.getAccountBalance() != null ? dto.getAccountBalance() : BigDecimal.ZERO);
        event.setAccountCurrency(dto.getAccountCurrency() != null ? dto.getAccountCurrency() : Currency.PLN);
        return fundraisingEventRepository.save(event);
    }


    public void assignCollectionBox(Long eventId, Long boxId) {
        FundraisingEvent event = fundraisingEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));
        CollectionBox box = collectionBoxRepository.findById(boxId)
                .orElseThrow(() -> new EntityNotFoundException("Box not found: " + boxId));

        boolean isEmpty = box.getCollectedAmounts().values().stream()
                .allMatch(amount -> amount.compareTo(BigDecimal.ZERO) == 0);

        if (!isEmpty) {
            throw new IllegalStateException("Box " + boxId + " is not empty and cannot be assigned.");
        }

        box.setFundraisingEvent(event);
        collectionBoxRepository.save(box);
    }

    public List<FundraisingEventReportDto> getFinancialReport() {
        return fundraisingEventRepository.findAll().stream()
                .map(event -> new FundraisingEventReportDto(
                        event.getName(),
                        event.getAccountBalance(),
                        event.getAccountCurrency()
                        ))
                .collect(Collectors.toList());
    }

}
