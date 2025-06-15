package com.charitybox.dto;

import java.util.List;

public class NbpTableDto {
    private String table;
    private String no;
    private String effectiveDate;
    private List<NbpRateDto> rates;

    public String getTable() { return table; }
    public void setTable(String table) { this.table = table; }
    public String getNo() { return no; }
    public void setNo(String no) { this.no = no; }
    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }
    public List<NbpRateDto> getRates() { return rates; }
    public void setRates(List<NbpRateDto> rates) { this.rates = rates; }
}
