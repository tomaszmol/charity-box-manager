package com.charitybox.service;

import com.charitybox.model.CollectionBox;
import com.charitybox.repository.CollectionBoxRepository;
import org.springframework.stereotype.Service;

@Service
public class CollectionBoxService {
    private final CollectionBoxRepository collectionBoxRepository;

    public CollectionBoxService (CollectionBoxRepository collectionBoxRepository){
        this.collectionBoxRepository = collectionBoxRepository;
    }

    public CollectionBox createBox(CollectionBox box){
        return collectionBoxRepository.save(box);
    }
}
