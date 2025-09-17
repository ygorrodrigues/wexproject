package com.ygorrodrigues.wexproject.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ExchangeRateApiResponse {
    
    @JsonProperty("data")
    private List<ExchangeRateData> data;
    
    // Constructors
    public ExchangeRateApiResponse() {}
    
    public ExchangeRateApiResponse(List<ExchangeRateData> data) {
        this.data = data;
    }
    
    // Getters and Setters
    public List<ExchangeRateData> getData() {
        return data;
    }
    
    public void setData(List<ExchangeRateData> data) {
        this.data = data;
    }
}
