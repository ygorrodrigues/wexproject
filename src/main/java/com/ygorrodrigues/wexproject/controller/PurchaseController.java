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
            @PathVariable("id") Integer id,
            @RequestParam("countryCurrency") String countryCurrency) {
        
        Purchase purchase = purchaseService.findById(id);
        if (purchase == null) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            ExchangeRateResponse response = exchangeRateService.calculateExchangeRate(countryCurrency, purchase);
            return ResponseEntity.ok(response);
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());
        }
    }
}
