package com.ygorrodrigues.wexproject.service;

import com.ygorrodrigues.wexproject.models.Purchase;
import com.ygorrodrigues.wexproject.models.PurchaseRequest;
import com.ygorrodrigues.wexproject.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PurchaseService {
    
    @Autowired
    private PurchaseRepository purchaseRepository;

    public Purchase processPurchase(PurchaseRequest request) {
        Purchase purchase = new Purchase(
            request.getDescription(),
            request.getAmount(),
            request.getTransactionDate()
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return savedPurchase;
    }
    
    public Purchase findById(Integer id) {
        Optional<Purchase> purchase = purchaseRepository.findById(id);
        return purchase.orElse(null);
    }
}
