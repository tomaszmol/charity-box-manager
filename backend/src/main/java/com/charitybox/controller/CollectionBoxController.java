package com.charitybox.controller;

import com.charitybox.dto.CollectionBoxDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.service.CollectionBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boxes")
public class CollectionBoxController {

    private final CollectionBoxService collectionBoxService;

    @Autowired
    public CollectionBoxController(CollectionBoxService collectionBoxService){
        this.collectionBoxService = collectionBoxService;
    }

    @PostMapping
    public CollectionBox createBox() {
        return collectionBoxService.createBox(new CollectionBox());
    }

    @GetMapping
    public List<CollectionBoxDto> listBoxes() {
        return collectionBoxService.listBoxes();
    }
}

