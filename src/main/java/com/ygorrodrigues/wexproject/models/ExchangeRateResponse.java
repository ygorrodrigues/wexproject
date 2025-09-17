package com.ygorrodrigues.wexproject.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ExchangeRateResponse {
    
    private UUID id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal originalAmount;
    private String originalCurrency;
    private BigDecimal convertedAmount;
    private String targetCurrency;
    private BigDecimal exchangeRate;
    
    // Constructors
    public ExchangeRateResponse() {}
    
    public ExchangeRateResponse(UUID id, String description, LocalDate transactionDate,
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
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }
    
    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }
    
    public String getOriginalCurrency() {
        return originalCurrency;
    }
    
    public void setOriginalCurrency(String originalCurrency) {
        this.originalCurrency = originalCurrency;
    }
    
    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
    
    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
    
    public String getTargetCurrency() {
        return targetCurrency;
    }
    
    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }
    
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
