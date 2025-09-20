package com.ygorrodrigues.wexproject.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {
    
    private Integer id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal originalAmount;
    private String originalCurrency;
    private BigDecimal convertedAmount;
    private String targetCurrency;
    private BigDecimal exchangeRate;
    
}
