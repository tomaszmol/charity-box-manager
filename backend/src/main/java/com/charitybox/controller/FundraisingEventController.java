package com.charitybox.controller;

import com.charitybox.dto.FundraisingEventDto;
import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.FundraisingEventRepository;
import com.charitybox.service.FundraisingEventService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/events")
public class FundraisingEventController {

    private final FundraisingEventService fundraisingEventService;

    @Autowired
    public FundraisingEventController(FundraisingEventRepository fundraisingEventRepository, FundraisingEventService fundraisingEventService) {
        this.fundraisingEventService = fundraisingEventService;
    }

    @PostMapping
    public ResponseEntity<FundraisingEvent> createEvent(@RequestBody FundraisingEventDto eventDto) {
        FundraisingEvent event = fundraisingEventService.createEvent(eventDto);
        return ResponseEntity
                .created(URI.create("/api/events/" + event.getId()))
                .body(event);
    }

    @PutMapping("/{eventId}/assign-box/{boxId}")
    public ResponseEntity<Void> assignCollectionBox(
            @PathVariable Long eventId,
            @PathVariable Long boxId) {
        fundraisingEventService.assignCollectionBox(eventId, boxId);
        return ResponseEntity.noContent().build();
    }
}