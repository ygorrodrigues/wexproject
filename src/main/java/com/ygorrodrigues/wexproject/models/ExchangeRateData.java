package com.ygorrodrigues.wexproject.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangeRateData {
    
    @JsonProperty("country_currency_desc")
    private String countryCurrencyDesc;
    
    @JsonProperty("exchange_rate")
    private String exchangeRate;
    
    @JsonProperty("record_date")
    private String recordDate;
    
    // Constructors
    public ExchangeRateData() {}
    
    public ExchangeRateData(String countryCurrencyDesc, String exchangeRate, String recordDate) {
        this.countryCurrencyDesc = countryCurrencyDesc;
        this.exchangeRate = exchangeRate;
        this.recordDate = recordDate;
    }
    
    // Getters and Setters
    public String getCountryCurrencyDesc() {
        return countryCurrencyDesc;
    }
    
    public void setCountryCurrencyDesc(String countryCurrencyDesc) {
        this.countryCurrencyDesc = countryCurrencyDesc;
    }
    
    public String getExchangeRate() {
        return exchangeRate;
    }
    
    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    
    public String getRecordDate() {
        return recordDate;
    }
    
    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
}
