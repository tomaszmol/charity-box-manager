package com.charitybox.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Entity
public class CollectionBox {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private FundraisingEvent fundraisingEvent;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "collection_box_amounts", joinColumns = @JoinColumn(name = "collection_box_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "currency")
    @Column(name = "amount")
    private Map<Currency, BigDecimal> collectedAmounts = new EnumMap<>(Currency.class);


    public CollectionBox() {
        for (Currency currency : Currency.values()) {
            collectedAmounts.put(currency, BigDecimal.ZERO);
        }
    }

    public Long getId() {
        return id;
    }

    public FundraisingEvent getFundraisingEvent() {
        return fundraisingEvent;
    }

    public void setFundraisingEvent(FundraisingEvent fundraisingEvent) {
        this.fundraisingEvent = fundraisingEvent;
    }


    public Map<Currency, BigDecimal> getCollectedAmounts() {
        return collectedAmounts;
    }

    public void setCollectedAmounts(Map<Currency, BigDecimal> collectedAmounts) {
        this.collectedAmounts = collectedAmounts;
    }
}