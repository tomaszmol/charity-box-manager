package com.charitybox.service;

import com.charitybox.dto.CollectionBoxDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.model.Currency;
import com.charitybox.repository.CollectionBoxRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionBoxService {
    private final CollectionBoxRepository collectionBoxRepository;

    public CollectionBoxService (CollectionBoxRepository collectionBoxRepository){
        this.collectionBoxRepository = collectionBoxRepository;
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
        if (!collectionBoxRepository.existsById(id)) {
            throw new EntityNotFoundException("CollectionBox not found: " + id);
        }
        collectionBoxRepository.deleteById(id);
    }

    public void addMoney(Long boxId, Currency currency, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be higher or equal to 0");
        }
        CollectionBox box = collectionBoxRepository.findById(boxId)
                .orElseThrow(() -> new EntityNotFoundException("Box not found: " + boxId));
        if (box.getFundraisingEvent() == null) {
            throw new IllegalStateException("Box is not assigned to any fundraising event. Adding money will prevent assigning this box to an event.");
        }
        box.getCollectedAmounts().merge(currency, amount, BigDecimal::add);
        collectionBoxRepository.save(box);
    }
}
