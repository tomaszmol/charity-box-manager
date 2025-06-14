package com.charitybox.controller;

import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.FundraisingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class FundraisingEventController {

    private final FundraisingEventRepository fundraisingEventRepository;

    @Autowired
    public FundraisingEventController(FundraisingEventRepository fundraisingEventRepository) {
        this.fundraisingEventRepository = fundraisingEventRepository;
    }

    @PostMapping
    public FundraisingEvent createEvent(@RequestBody FundraisingEvent event) {
        return fundraisingEventRepository.save(event);
    }
}