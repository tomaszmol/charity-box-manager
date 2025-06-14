package com.charitybox.service;

import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.FundraisingEventRepository;
import org.springframework.stereotype.Service;

@Service
public class FundraisingEventService {
    private final FundraisingEventRepository fundraisingEventRepository;

    public FundraisingEventService(FundraisingEventRepository fundraisingEventRepository){
        this.fundraisingEventRepository = fundraisingEventRepository;
    }

    public FundraisingEvent createEvent(FundraisingEvent event){
        return fundraisingEventRepository.save(event);
    }

}
