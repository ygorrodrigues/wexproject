package com.ygorrodrigues.wexproject.service;

import com.ygorrodrigues.wexproject.exception.SavePurchaseException;
import com.ygorrodrigues.wexproject.models.Purchase;
import com.ygorrodrigues.wexproject.models.PurchaseRequest;
import com.ygorrodrigues.wexproject.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void processPurchase_ShouldReturnSavedPurchase_WhenValidRequest() {
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(getTestPurchase());

        Purchase result = purchaseService.processPurchase(getTestPurchaseRequest());

        assertNotNull(result);
        assertEquals("Test Purchase", result.getDescription());
        assertEquals(new BigDecimal("100"), result.getAmount());
        assertEquals(LocalDate.of(2025, 9, 15), result.getTransactionDate());
        
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void processPurchase_ShouldHandleRepositoryException_WhenSaveFails() {
        when(purchaseRepository.save(any(Purchase.class)))
            .thenThrow(new RuntimeException("Database error"));

        assertThrows(SavePurchaseException.class, () -> {
            purchaseService.processPurchase(getTestPurchaseRequest());
        });
        
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void findById_ShouldReturnPurchase_WhenPurchaseExists() {
        int testId = 1;
        when(purchaseRepository.findById(testId)).thenReturn(Optional.of(getTestPurchase()));

        Purchase result = purchaseService.findById(testId);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Test Purchase", result.getDescription());
        assertEquals(new BigDecimal("100"), result.getAmount());
        assertEquals(LocalDate.of(2025, 9, 15), result.getTransactionDate());
        
        verify(purchaseRepository, times(1)).findById(testId);
    }

    @Test
    void findById_ShouldReturnNull_WhenPurchaseDoesNotExist() {
        int testId = 1;
        when(purchaseRepository.findById(testId)).thenReturn(Optional.empty());

        Purchase result = purchaseService.findById(testId);

        assertNull(result);
        verify(purchaseRepository, times(1)).findById(testId);
    }


    @Test
    void findById_ShouldHandleRepositoryException_WhenFindByIdFails() {
        int testId = 1;
        when(purchaseRepository.findById(testId))
            .thenThrow(new RuntimeException("Database error"));

        Purchase result = purchaseService.findById(testId);

        assertNull(result);
        verify(purchaseRepository, times(1)).findById(testId);
    }

    private PurchaseRequest getTestPurchaseRequest() {
        return PurchaseRequest.builder()
            .description("Test Purchase")
            .amount(new BigDecimal("100"))
            .transactionDate(LocalDate.of(2025, 9, 15))
            .build();
    }

    private Purchase getTestPurchase() {
        return Purchase.builder()
            .id(1)
            .description("Test Purchase")
            .amount(new BigDecimal("100"))
            .transactionDate(LocalDate.of(2025, 9, 15))
            .build();
    }

}
