package com.ygorrodrigues.wexproject.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ExchangeRateResponse {
    
    private Integer id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal originalAmount;
    private String originalCurrency;
    private BigDecimal convertedAmount;
    private String targetCurrency;
    private BigDecimal exchangeRate;
    
    // Constructors
    public ExchangeRateResponse() {}
    
    public ExchangeRateResponse(Integer id, String description, LocalDate transactionDate,
                              BigDecimal originalAmount, String originalCurrency,
                              BigDecimal convertedAmount, String targetCurrency,
                              BigDecimal exchangeRate) {
        this.id = id;
        this.description = description;
        this.transactionDate = transactionDate;
        this.originalAmount = originalAmount;
        this.originalCurrency = originalCurrency;
        this.convertedAmount = convertedAmount;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }
    
    // Getters
    public Integer getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }
    
    public String getOriginalCurrency() {
        return originalCurrency;
    }
    
    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
    
    public String getTargetCurrency() {
        return targetCurrency;
    }
    
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    
}
