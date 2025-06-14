package com.charitybox.controller;

import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.FundraisingEventRepository;
import com.charitybox.service.FundraisingEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class FundraisingEventController {

    private final FundraisingEventService fundraisingEventService;

    @Autowired
    public FundraisingEventController(FundraisingEventRepository fundraisingEventRepository, FundraisingEventService fundraisingEventService) {
        this.fundraisingEventService = fundraisingEventService;
    }

    @PostMapping
    public FundraisingEvent createEvent(@RequestBody FundraisingEvent event) {
        return fundraisingEventService.createEvent(event);
    }
}