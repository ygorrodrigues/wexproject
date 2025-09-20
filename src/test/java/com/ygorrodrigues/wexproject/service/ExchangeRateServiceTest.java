package com.ygorrodrigues.wexproject.service;

import com.ygorrodrigues.wexproject.exception.CurrencyNotFoundException;
import com.ygorrodrigues.wexproject.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ExchangeRateService exchangeRateService;

    private Purchase testPurchase;
    private ExchangeRateApiResponse mockApiResponse;
    private ExchangeRateData mockExchangeRateData;
    private Integer testId;

    @BeforeEach
    void setUp() {
        testId = 1;
        testPurchase = new Purchase(
            "Test Purchase",
            new BigDecimal("100.00"),
            LocalDate.of(2024, 1, 15)
        );
        testPurchase.setId(testId);

        mockExchangeRateData = new ExchangeRateData(
            "Canada-Dollar",
            "1.25",
            "2024-01-15"
        );

        mockApiResponse = new ExchangeRateApiResponse(
            Arrays.asList(mockExchangeRateData)
        );
        
        // Create ExchangeRateService with mocked RestTemplate
        exchangeRateService = new ExchangeRateService();
        // Use reflection to inject the mocked RestTemplate
        try {
            java.lang.reflect.Field restTemplateField = ExchangeRateService.class.getDeclaredField("restTemplate");
            restTemplateField.setAccessible(true);
            restTemplateField.set(exchangeRateService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocked RestTemplate", e);
        }
    }

    @Test
    void calculateExchangeRate_ShouldReturnCorrectResponse_WhenValidData() {
        // Arrange
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act
        ExchangeRateResponse result = exchangeRateService.calculateExchangeRate("Canada-Dollar", testPurchase);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Test Purchase", result.getDescription());
        assertEquals(LocalDate.of(2024, 1, 15), result.getTransactionDate());
        assertEquals(new BigDecimal("100.00"), result.getOriginalAmount());
        assertEquals("USD", result.getOriginalCurrency());
        assertEquals(new BigDecimal("125.00"), result.getConvertedAmount());
        assertEquals("Canada-Dollar", result.getTargetCurrency());
        assertEquals(new BigDecimal("1.25"), result.getExchangeRate());
    }

    @Test
    void calculateExchangeRate_ShouldRoundCorrectly_WhenConversionHasManyDecimals() {
        // Arrange
        ExchangeRateData dataWithManyDecimals = new ExchangeRateData(
            "Japan-Yen",
            "0.0067",
            "2024-01-15"
        );
        ExchangeRateApiResponse apiResponse = new ExchangeRateApiResponse(
            Arrays.asList(dataWithManyDecimals)
        );
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        Purchase purchaseWithDecimals = new Purchase(
            "Test Purchase",
            new BigDecimal("100.123"),
            LocalDate.of(2024, 1, 15)
        );
        purchaseWithDecimals.setId(testId);

        // Act
        ExchangeRateResponse result = exchangeRateService.calculateExchangeRate("Japan-Yen", purchaseWithDecimals);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("0.67"), result.getConvertedAmount()); // Should be rounded to 2 decimal places
        assertEquals(new BigDecimal("0.0067"), result.getExchangeRate());
    }

    @Test
    void calculateExchangeRate_ShouldThrowCurrencyNotFoundException_WhenCurrencyNotFound() {
        // Arrange
        ExchangeRateApiResponse emptyResponse = new ExchangeRateApiResponse(Collections.emptyList());
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(emptyResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act & Assert
        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.calculateExchangeRate("Invalid-Currency", testPurchase);
        });
        
        assertTrue(exception.getMessage().contains("Purchase cannot be converted to the target currency: Invalid-Currency"));
    }

    @Test
    void calculateExchangeRate_ShouldThrowCurrencyNotFoundException_WhenApiCallFails() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenThrow(new RestClientException("API Error"));

        // Act & Assert
        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.calculateExchangeRate("Canada-Dollar", testPurchase);
        });
        
        assertTrue(exception.getMessage().contains("Unable to fetch exchange rate for currency: Canada-Dollar") ||
                   exception.getMessage().contains("Purchase cannot be converted to the target currency: Canada-Dollar"));
    }

    @Test
    void calculateExchangeRate_ShouldThrowCurrencyNotFoundException_WhenResponseBodyIsNull() {
        // Arrange
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            ResponseEntity.ok(null);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act & Assert
        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.calculateExchangeRate("Canada-Dollar", testPurchase);
        });
        
        assertTrue(exception.getMessage().contains("Purchase cannot be converted to the target currency: Canada-Dollar"));
    }

    @Test
    void getExchangeRate_ShouldReturnCorrectRate_WhenValidData() {
        // Arrange
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act
        BigDecimal result = exchangeRateService.getExchangeRate("Canada-Dollar", LocalDate.of(2024, 1, 15));

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1.25"), result);
    }

    @Test
    void getExchangeRate_ShouldUseCorrectUrl_WhenCalled() {
        // Arrange
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act
        exchangeRateService.getExchangeRate("Canada-Dollar", LocalDate.of(2024, 1, 15));

        // Assert
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(ExchangeRateApiResponse.class));
    }

    @Test
    void getExchangeRate_ShouldReturnMostRecentRate_WhenMultipleRatesAvailable() {
        // Arrange
        ExchangeRateData olderRate = new ExchangeRateData(
            "Canada-Dollar",
            "1.20",
            "2023-12-15"
        );
        ExchangeRateData newerRate = new ExchangeRateData(
            "Canada-Dollar",
            "1.25",
            "2024-01-15"
        );
        
        ExchangeRateApiResponse multipleRatesResponse = new ExchangeRateApiResponse(
            Arrays.asList(newerRate, olderRate) // Newer rate first due to sorting
        );
        
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(multipleRatesResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act
        BigDecimal result = exchangeRateService.getExchangeRate("Canada-Dollar", LocalDate.of(2024, 1, 15));

        // Assert
        assertEquals(new BigDecimal("1.25"), result); // Should return the newer rate
    }

    @Test
    void getExchangeRate_ShouldThrowCurrencyNotFoundException_WhenNoDataFound() {
        // Arrange
        ExchangeRateApiResponse emptyResponse = new ExchangeRateApiResponse(Collections.emptyList());
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(emptyResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act & Assert
        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.getExchangeRate("Invalid-Currency", LocalDate.of(2024, 1, 15));
        });
        
        assertTrue(exception.getMessage().contains("Exchange rate data not found for currency: Invalid-Currency"));
    }

    @Test
    void getExchangeRate_ShouldHandleHttpError_WhenApiReturnsError() {
        // Arrange
        ResponseEntity<ExchangeRateApiResponse> errorResponse = 
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(errorResponse);

        // Act & Assert
        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.getExchangeRate("Canada-Dollar", LocalDate.of(2024, 1, 15));
        });
        
        assertTrue(exception.getMessage().contains("Unable to fetch exchange rate for currency: Canada-Dollar") ||
                   exception.getMessage().contains("Exchange rate data not found for currency: Canada-Dollar"));
    }

    @Test
    void getExchangeRate_ShouldHandleMalformedExchangeRate_WhenRateIsInvalid() {
        // Arrange
        ExchangeRateData invalidRateData = new ExchangeRateData(
            "Canada-Dollar",
            "invalid-rate",
            "2024-01-15"
        );
        ExchangeRateApiResponse invalidResponse = new ExchangeRateApiResponse(
            Arrays.asList(invalidRateData)
        );
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(invalidResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act & Assert
        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.getExchangeRate("Canada-Dollar", LocalDate.of(2024, 1, 15));
        });
        
        assertTrue(exception.getMessage().contains("Unable to fetch exchange rate for currency: Canada-Dollar"));
    }

    @Test
    void calculateExchangeRate_ShouldHandleZeroAmount_WhenAmountIsZero() {
        // Arrange
        Purchase zeroAmountPurchase = new Purchase(
            "Zero Purchase",
            BigDecimal.ZERO,
            LocalDate.of(2024, 1, 15)
        );
        zeroAmountPurchase.setId(testId);

        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act
        ExchangeRateResponse result = exchangeRateService.calculateExchangeRate("Canada-Dollar", zeroAmountPurchase);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("0.00"), result.getConvertedAmount());
        assertEquals(new BigDecimal("1.25"), result.getExchangeRate());
    }

    @Test
    void calculateExchangeRate_ShouldHandleVeryLargeAmount_WhenAmountIsVeryLarge() {
        // Arrange
        Purchase largeAmountPurchase = new Purchase(
            "Large Purchase",
            new BigDecimal("999999999.99"),
            LocalDate.of(2024, 1, 15)
        );
        largeAmountPurchase.setId(testId);

        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act
        ExchangeRateResponse result = exchangeRateService.calculateExchangeRate("Canada-Dollar", largeAmountPurchase);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1249999999.99"), result.getConvertedAmount());
        assertEquals(new BigDecimal("1.25"), result.getExchangeRate());
    }

    @Test
    void getExchangeRate_ShouldCalculateCorrectSixMonthsBefore_WhenTransactionDateIsProvided() {
        // Arrange
        LocalDate transactionDate = LocalDate.of(2024, 6, 15);
        
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        // Act
        BigDecimal result = exchangeRateService.getExchangeRate("Canada-Dollar", transactionDate);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1.25"), result);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(ExchangeRateApiResponse.class));
    }

    @Test
    void calculateExchangeRate_ShouldHandleNullPurchase_WhenPurchaseIsNull() {
        // Act & Assert
        assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.calculateExchangeRate("Canada-Dollar", null);
        });
    }

    @Test
    void getExchangeRate_ShouldHandleNullCurrency_WhenCurrencyIsNull() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            exchangeRateService.getExchangeRate(null, LocalDate.of(2024, 1, 15));
        });
    }
}
