package com.charitybox.controller;

import com.charitybox.dto.AddMoneyRequest;
import com.charitybox.dto.CollectionBoxDto;
import com.charitybox.model.CollectionBox;
import com.charitybox.model.Currency;
import com.charitybox.service.CollectionBoxService;
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

@WebMvcTest(CollectionBoxController.class)
class CollectionBoxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CollectionBoxService collectionBoxService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBox_shouldReturnCreatedBox() throws Exception {
        CollectionBox box = new CollectionBox();
        box.setId(123L);
        Mockito.when(collectionBoxService.createBox(any())).thenReturn(box);

        mockMvc.perform(post("/api/boxes"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/boxes/123"))
                .andExpect(jsonPath("$.id").value(123));
    }

    @Test
    void listBoxes_shouldReturnList() throws Exception {
        CollectionBoxDto dto = new CollectionBoxDto(1L, true, true);
        Mockito.when(collectionBoxService.listBoxes()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/boxes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void deleteBox_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/boxes/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(collectionBoxService).deleteBox(1L);
    }

    @Test
    void addMoney_shouldReturnNoContent() throws Exception {
        AddMoneyRequest req = new AddMoneyRequest();
        req.setCurrency("PLN");
        req.setAmount(new BigDecimal("10"));

        mockMvc.perform(put("/api/boxes/1/add-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        Mockito.verify(collectionBoxService).addMoney(1L, Currency.PLN.toString(), new BigDecimal("10"));
    }


    @Test
    void addMoney_shouldReturnBadRequestForInvalidCurrency() throws Exception {
        AddMoneyRequest req = new AddMoneyRequest();
        req.setCurrency("XXX");
        req.setAmount(new BigDecimal("10"));

        Mockito.doThrow(new IllegalArgumentException("Unsupported currency: XXX"))
                .when(collectionBoxService).addMoney(anyLong(), eq("XXX"), any());

        mockMvc.perform(put("/api/boxes/1/add-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyBox_shouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/api/boxes/1/empty"))
                .andExpect(status().isNoContent());
        Mockito.verify(collectionBoxService).emptyBox(1L);
    }
}