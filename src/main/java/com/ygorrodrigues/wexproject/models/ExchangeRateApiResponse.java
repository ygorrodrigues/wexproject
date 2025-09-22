package com.ygorrodrigues.wexproject.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateApiResponse {
    
    @JsonProperty("data")
    private List<ExchangeRateData> data;
    
}
