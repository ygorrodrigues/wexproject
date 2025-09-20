package com.ygorrodrigues.wexproject.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateData {
    
    @JsonProperty("country_currency_desc")
    private String countryCurrencyDesc;
    
    @JsonProperty("exchange_rate")
    private String exchangeRate;
    
    @JsonProperty("record_date")
    private String recordDate;

}
