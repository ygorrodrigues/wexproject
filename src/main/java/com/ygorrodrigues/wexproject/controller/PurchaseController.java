package com.ygorrodrigues.wexproject.controller;

import com.ygorrodrigues.wexproject.models.PurchaseRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseController {
    
    @PostMapping("/purchase")
    public String purchase(@Valid @RequestBody PurchaseRequest request) {
        System.out.println(request.getDescription());
        System.out.println(request.getAmount());
        System.out.println(request.getTransactionDate());
        return "Purchase processed successfully";
    }
}
