package com.ygorrodrigues.wexproject.service;

import com.ygorrodrigues.wexproject.models.Purchase;
import com.ygorrodrigues.wexproject.models.PurchaseRequest;
import com.ygorrodrigues.wexproject.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private PurchaseRequest purchaseRequest;
    private Purchase purchase;
    private Integer testId;

    @BeforeEach
    void setUp() {
        testId = 1;
        purchaseRequest = new PurchaseRequest(
            "Test Purchase",
            new BigDecimal("100.50"),
            LocalDate.of(2024, 1, 15)
        );
        
        purchase = new Purchase(
            "Test Purchase",
            new BigDecimal("100.50"),
            LocalDate.of(2024, 1, 15)
        );
        purchase.setId(testId);
    }

    @Test
    void processPurchase_ShouldReturnSavedPurchase_WhenValidRequest() {
        // Arrange
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

        // Act
        Purchase result = purchaseService.processPurchase(purchaseRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Test Purchase", result.getDescription());
        assertEquals(new BigDecimal("100.50"), result.getAmount());
        assertEquals(LocalDate.of(2024, 1, 15), result.getTransactionDate());
        
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void processPurchase_ShouldCreatePurchaseWithCorrectData_WhenValidRequest() {
        // Arrange
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> {
            Purchase savedPurchase = invocation.getArgument(0);
            savedPurchase.setId(testId);
            return savedPurchase;
        });

        // Act
        Purchase result = purchaseService.processPurchase(purchaseRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Purchase", result.getDescription());
        assertEquals(new BigDecimal("100.50"), result.getAmount());
        assertEquals(LocalDate.of(2024, 1, 15), result.getTransactionDate());
        
        verify(purchaseRepository, times(1)).save(argThat(p -> 
            "Test Purchase".equals(p.getDescription()) &&
            new BigDecimal("100.50").equals(p.getAmount()) &&
            LocalDate.of(2024, 1, 15).equals(p.getTransactionDate())
        ));
    }

    @Test
    void processPurchase_ShouldHandleNullRequest_WhenRequestIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            purchaseService.processPurchase(null);
        });
    }

    @Test
    void processPurchase_ShouldHandleRepositoryException_WhenSaveFails() {
        // Arrange
        when(purchaseRepository.save(any(Purchase.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            purchaseService.processPurchase(purchaseRequest);
        });
        
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void findById_ShouldReturnPurchase_WhenPurchaseExists() {
        // Arrange
        when(purchaseRepository.findById(testId)).thenReturn(Optional.of(purchase));

        // Act
        Purchase result = purchaseService.findById(testId);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Test Purchase", result.getDescription());
        assertEquals(new BigDecimal("100.50"), result.getAmount());
        assertEquals(LocalDate.of(2024, 1, 15), result.getTransactionDate());
        
        verify(purchaseRepository, times(1)).findById(testId);
    }

    @Test
    void findById_ShouldReturnNull_WhenPurchaseDoesNotExist() {
        // Arrange
        when(purchaseRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        Purchase result = purchaseService.findById(testId);

        // Assert
        assertNull(result);
        verify(purchaseRepository, times(1)).findById(testId);
    }


    @Test
    void findById_ShouldHandleRepositoryException_WhenFindByIdFails() {
        // Arrange
        when(purchaseRepository.findById(testId))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            purchaseService.findById(testId);
        });
        
        verify(purchaseRepository, times(1)).findById(testId);
    }

    @Test
    void processPurchase_ShouldHandleLargeAmounts_WhenAmountIsVeryLarge() {
        // Arrange
        PurchaseRequest largeAmountRequest = new PurchaseRequest(
            "Large Purchase",
            new BigDecimal("999999999.99"),
            LocalDate.of(2024, 1, 15)
        );
        
        Purchase largeAmountPurchase = new Purchase(
            "Large Purchase",
            new BigDecimal("999999999.99"),
            LocalDate.of(2024, 1, 15)
        );
        largeAmountPurchase.setId(testId);
        
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(largeAmountPurchase);

        // Act
        Purchase result = purchaseService.processPurchase(largeAmountRequest);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("999999999.99"), result.getAmount());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void processPurchase_ShouldHandleSmallAmounts_WhenAmountIsVerySmall() {
        // Arrange
        PurchaseRequest smallAmountRequest = new PurchaseRequest(
            "Small Purchase",
            new BigDecimal("0.01"),
            LocalDate.of(2024, 1, 15)
        );
        
        Purchase smallAmountPurchase = new Purchase(
            "Small Purchase",
            new BigDecimal("0.01"),
            LocalDate.of(2024, 1, 15)
        );
        smallAmountPurchase.setId(testId);
        
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(smallAmountPurchase);

        // Act
        Purchase result = purchaseService.processPurchase(smallAmountRequest);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("0.01"), result.getAmount());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void processPurchase_ShouldHandleFutureDates_WhenTransactionDateIsInFuture() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(30);
        PurchaseRequest futureDateRequest = new PurchaseRequest(
            "Future Purchase",
            new BigDecimal("50.00"),
            futureDate
        );
        
        Purchase futureDatePurchase = new Purchase(
            "Future Purchase",
            new BigDecimal("50.00"),
            futureDate
        );
        futureDatePurchase.setId(testId);
        
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(futureDatePurchase);

        // Act
        Purchase result = purchaseService.processPurchase(futureDateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(futureDate, result.getTransactionDate());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void processPurchase_ShouldHandlePastDates_WhenTransactionDateIsInPast() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(365);
        PurchaseRequest pastDateRequest = new PurchaseRequest(
            "Past Purchase",
            new BigDecimal("25.00"),
            pastDate
        );
        
        Purchase pastDatePurchase = new Purchase(
            "Past Purchase",
            new BigDecimal("25.00"),
            pastDate
        );
        pastDatePurchase.setId(testId);
        
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(pastDatePurchase);

        // Act
        Purchase result = purchaseService.processPurchase(pastDateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(pastDate, result.getTransactionDate());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }
}
