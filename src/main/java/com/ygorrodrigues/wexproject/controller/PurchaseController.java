package com.ygorrodrigues.wexproject.controller;

import com.ygorrodrigues.wexproject.exception.CurrencyNotFoundException;
import com.ygorrodrigues.wexproject.models.ExchangeRateResponse;
import com.ygorrodrigues.wexproject.models.Purchase;
import com.ygorrodrigues.wexproject.models.PurchaseRequest;
import com.ygorrodrigues.wexproject.service.ExchangeRateService;
import com.ygorrodrigues.wexproject.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@RestController
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    @PostMapping("/purchase")
    public ResponseEntity<Purchase> purchase(@Valid @RequestBody PurchaseRequest purchaseRequest) {
        Purchase savedPurchase = purchaseService.processPurchase(purchaseRequest);
        return ResponseEntity.ok(savedPurchase);
    }
    
    @GetMapping("/purchase/{id}/exchange")
    public ResponseEntity<?> getExchangeRate(
            @PathVariable UUID id,
            @RequestParam String countryCurrency) {
        
        Purchase purchase = purchaseService.findById(id);
        if (purchase == null) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate(countryCurrency, purchase.getTransactionDate());
            BigDecimal convertedAmount = purchase.getAmount().multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP); // Round to 2 decimal places (cents)
            
            ExchangeRateResponse response = new ExchangeRateResponse(
                purchase.getId(),
                purchase.getDescription(),
                purchase.getTransactionDate(),
                purchase.getAmount(),
                "USD",
                convertedAmount,
                countryCurrency,
                exchangeRate
            );
            
            return ResponseEntity.ok(response);
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Purchase cannot be converted to the target currency: " + e.getMessage());
        }
    }
}
