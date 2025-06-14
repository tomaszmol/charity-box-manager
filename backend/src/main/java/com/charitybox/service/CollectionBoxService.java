package com.charitybox.service;

import com.charitybox.dto.CollectionBoxDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.repository.CollectionBoxRepository;
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
}
