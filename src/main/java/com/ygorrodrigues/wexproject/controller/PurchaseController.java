package com.ygorrodrigues.wexproject.controller;

import com.ygorrodrigues.wexproject.models.Purchase;
import com.ygorrodrigues.wexproject.models.PurchaseRequest;
import com.ygorrodrigues.wexproject.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;
    
    @PostMapping("/purchase")
    public ResponseEntity<Purchase> purchase(@Valid @RequestBody PurchaseRequest purchaseRequest) {
        Purchase savedPurchase = purchaseService.processPurchase(purchaseRequest);
        return ResponseEntity.ok(savedPurchase);
    }
}
