package com.charitybox.dto;

import java.math.BigDecimal;

public class NbpRateDto {
    private String currency;
    private String code;
    private BigDecimal mid;


    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getMid() { return mid; }
    public void setMid(BigDecimal mid) { this.mid = mid; }
}
