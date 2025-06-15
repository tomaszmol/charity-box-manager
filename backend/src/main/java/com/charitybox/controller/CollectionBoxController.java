package com.charitybox.controller;

import com.charitybox.dto.AddMoneyRequest;
import com.charitybox.dto.CollectionBoxDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.model.Currency;
import com.charitybox.service.CollectionBoxService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBox(@PathVariable Long id) {
        collectionBoxService.deleteBox(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/add-money")
    public ResponseEntity<Void> addMoney(
            @PathVariable Long id,
            @RequestBody AddMoneyRequest request) {
        Currency currency;
        try {
            currency = Currency.valueOf(request.getCurrency());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Non-existing currency: " + request.getCurrency());
        }
        collectionBoxService.addMoney(id, currency, request.getAmount());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/empty")
    public ResponseEntity<Void> emptyBox(@PathVariable Long id) {
        collectionBoxService.emptyBox(id);
        return ResponseEntity.noContent().build();
    }
}

