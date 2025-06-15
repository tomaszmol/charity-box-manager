package com.charitybox.controller;

import com.charitybox.dto.FundraisingEventDto;
import com.charitybox.dto.FundraisingEventReportDto;
import com.charitybox.model.Currency;
import com.charitybox.model.FundraisingEvent;
import com.charitybox.service.FundraisingEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FundraisingEventController.class)
class FundraisingEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FundraisingEventService fundraisingEventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createEvent_shouldReturnCreatedEvent() throws Exception {
        FundraisingEventDto dto = new FundraisingEventDto();
        dto.setName("Test Event");
        FundraisingEvent event = new FundraisingEvent();
        event.setId(42L);
        event.setName("Test Event");
        Mockito.when(fundraisingEventService.createEvent(any())).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/events/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void assignCollectionBox_shouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/api/events/1/assign-box/2"))
                .andExpect(status().isNoContent());
        Mockito.verify(fundraisingEventService).assignCollectionBox(1L, 2L);
    }

    @Test
    void getFinancialReport_shouldReturnList() throws Exception {
        FundraisingEventReportDto report = new FundraisingEventReportDto("Event1", new BigDecimal("100.00"), Currency.PLN);
        Mockito.when(fundraisingEventService.getFinancialReport()).thenReturn(List.of(report));

        mockMvc.perform(get("/api/events/financial-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Event1"))
                .andExpect(jsonPath("$[0].accountBalance").value(100.00))
                .andExpect(jsonPath("$[0].accountCurrency").value("PLN"));
    }
}