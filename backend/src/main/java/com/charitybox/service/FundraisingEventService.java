package com.charitybox.service;

import com.charitybox.dto.FundraisingEventDto;
import com.charitybox.model.Currency;
import com.charitybox.model.FundraisingEvent;
import com.charitybox.repository.FundraisingEventRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FundraisingEventService {
    private final FundraisingEventRepository fundraisingEventRepository;

    public FundraisingEventService(FundraisingEventRepository fundraisingEventRepository){
        this.fundraisingEventRepository = fundraisingEventRepository;
    }

    public FundraisingEvent createEvent(FundraisingEventDto dto){
        FundraisingEvent event = new FundraisingEvent();
        event.setName(dto.getName());
        event.setAccountBalance(dto.getAccountBalance() != null ? dto.getAccountBalance() : BigDecimal.ZERO);
        event.setAccountCurrency(dto.getAccountCurrency() != null ? dto.getAccountCurrency() : Currency.PLN);
        return fundraisingEventRepository.save(event);
    }

}
