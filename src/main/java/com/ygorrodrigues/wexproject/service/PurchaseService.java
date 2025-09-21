package com.ygorrodrigues.wexproject.service;

import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ygorrodrigues.wexproject.exception.SavePurchaseException;
import com.ygorrodrigues.wexproject.models.Purchase;
import com.ygorrodrigues.wexproject.models.PurchaseRequest;
import com.ygorrodrigues.wexproject.repository.PurchaseRepository;

@Service
public class PurchaseService {
    
    @Autowired
    private PurchaseRepository purchaseRepository;

    public Purchase processPurchase(PurchaseRequest request) {
        Purchase purchase = Purchase.builder()
            .description(request.getDescription())
            .amount(request.getAmount().setScale(2, RoundingMode.HALF_UP))
            .transactionDate(request.getTransactionDate())
            .build();

        try {
            return purchaseRepository.save(purchase);
        } catch (Exception e) {
            throw new SavePurchaseException("Error while saving purchase.");
        }
    }
    
    public Purchase findById(Integer id) {
        try {
            Optional<Purchase> purchase = purchaseRepository.findById(id);
            return purchase.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
