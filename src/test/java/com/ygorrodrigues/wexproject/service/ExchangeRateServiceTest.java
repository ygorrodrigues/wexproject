package com.ygorrodrigues.wexproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ygorrodrigues.wexproject.exception.CurrencyNotFoundException;
import com.ygorrodrigues.wexproject.models.ExchangeRateApiResponse;
import com.ygorrodrigues.wexproject.models.ExchangeRateData;
import com.ygorrodrigues.wexproject.models.ExchangeRateResponse;
import com.ygorrodrigues.wexproject.models.Purchase;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
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
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(getCanadaDollarExchangeRate(), HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        ExchangeRateResponse result = exchangeRateService.calculateExchangeRate("Canada-Dollar", getTestPurchase());

        assertNotNull(result);
        assertEquals("Test Purchase", result.getDescription());
        assertEquals(LocalDate.of(2025, 9, 15), result.getTransactionDate());
        assertEquals(new BigDecimal("100"), result.getOriginalAmount());
        assertEquals("USD", result.getOriginalCurrency());
        assertEquals(new BigDecimal("125.00"), result.getConvertedAmount());
        assertEquals("Canada-Dollar", result.getTargetCurrency());
        assertEquals(new BigDecimal("1.25"), result.getExchangeRate());
    }

    @Test
    void calculateExchangeRate_ShouldRoundCorrectly_WhenConversionHasManyDecimals() {
        ExchangeRateApiResponse apiResponse = getUnitedKingdomPoundExchangeRate();
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(apiResponse, HttpStatus.OK);
        
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        ExchangeRateResponse result = exchangeRateService.calculateExchangeRate("United Kingdom-Pound", getTestPurchase());

        assertNotNull(result);
        assertEquals(new BigDecimal("100"), result.getOriginalAmount());
        assertEquals(new BigDecimal("73.67"), result.getConvertedAmount()); // Round after multiply
        assertEquals(new BigDecimal("0.73666"), result.getExchangeRate());
    }

    @Test
    void calculateExchangeRate_ShouldThrowCurrencyNotFoundException_WhenCurrencyNotFound() {
        ExchangeRateApiResponse emptyResponse = new ExchangeRateApiResponse(Collections.emptyList());
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(emptyResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.calculateExchangeRate("Invalid-Currency", getTestPurchase());
        });
        
        assertTrue(exception.getMessage().contains("Purchase cannot be converted to the target currency: Invalid-Currency"));
    }

    @Test
    void calculateExchangeRate_ShouldThrowCurrencyNotFoundException_WhenApiCallFails() {
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenThrow(new RestClientException("API Error"));

        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.calculateExchangeRate("Canada-Dollar", getTestPurchase());
        });
        
        assertTrue(exception.getMessage().contains("Unable to fetch exchange rate for currency: Canada-Dollar") ||
                   exception.getMessage().contains("Purchase cannot be converted to the target currency: Canada-Dollar"));
    }

    @Test
    void calculateExchangeRate_ShouldThrowCurrencyNotFoundException_WhenResponseBodyIsNull() {
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            ResponseEntity.ok(null);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.calculateExchangeRate("Canada-Dollar", getTestPurchase());
        });
        
        assertTrue(exception.getMessage().contains("Purchase cannot be converted to the target currency: Canada-Dollar"));
    }

    @Test
    void getExchangeRate_ShouldThrowCurrencyNotFoundException_WhenNoDataFound() {
        ExchangeRateApiResponse emptyResponse = new ExchangeRateApiResponse(Collections.emptyList());
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(emptyResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.getExchangeRate("Invalid-Currency", LocalDate.of(2025, 9, 15));
        });
        
        assertTrue(exception.getMessage().contains("Exchange rate data not found for currency: Invalid-Currency"));
    }

    @Test
    void getExchangeRate_ShouldHandleMalformedExchangeRate_WhenRateIsInvalid() {
        ExchangeRateApiResponse invalidResponse = ExchangeRateApiResponse.builder()
            .data(List.of(
                ExchangeRateData.builder()
                    .countryCurrencyDesc("Canada-Dollar")
                    .exchangeRate("invalid")
                    .recordDate("2025-09-15")
                    .build()
            ))
            .build();
        ResponseEntity<ExchangeRateApiResponse> responseEntity = 
            new ResponseEntity<>(invalidResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.getExchangeRate("Canada-Dollar", LocalDate.of(2025, 9, 15));
        });
        
        assertTrue(exception.getMessage().contains("Unable to fetch exchange rate for currency: Canada-Dollar"));
    }

    @Test
    void calculateExchangeRate_ShouldHandleNullPurchase_WhenPurchaseIsNull() {
        assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeRateService.calculateExchangeRate("Canada-Dollar", null);
        });
    }

    @Test
    void getExchangeRate_ShouldHandleNullCurrency_WhenCurrencyIsNull() {
        assertThrows(Exception.class, () -> {
            exchangeRateService.getExchangeRate(null, LocalDate.of(2025, 9, 15));
        });
    }

    @Test
    void getExchangeRate_ShouldIncludeGteAndLteDateFiltersInUrl() {
        LocalDate txDate = LocalDate.of(2025, 9, 15);
        LocalDate sixMonthsBefore = txDate.minusMonths(6);
        ResponseEntity<ExchangeRateApiResponse> responseEntity =
            new ResponseEntity<>(getCanadaDollarExchangeRate(), HttpStatus.OK);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        when(restTemplate.getForEntity(urlCaptor.capture(), eq(ExchangeRateApiResponse.class)))
            .thenReturn(responseEntity);

        BigDecimal rate = exchangeRateService.getExchangeRate("Canada-Dollar", txDate);

        assertEquals(new BigDecimal("1.25"), rate);
        String url = urlCaptor.getValue();
        assertTrue(url.contains("record_date:gte:" + sixMonthsBefore));
        assertTrue(url.contains("record_date:lte:" + txDate));
        assertTrue(url.contains("sort=-record_date"));
    }

    private Purchase getTestPurchase() {
        return Purchase.builder()
            .id(1)
            .description("Test Purchase")
            .amount(new BigDecimal("100"))
            .transactionDate(LocalDate.of(2025, 9, 15))
            .build();
    }

    private ExchangeRateApiResponse getCanadaDollarExchangeRate() {
        return ExchangeRateApiResponse.builder()
            .data(List.of(
                ExchangeRateData.builder()
                .countryCurrencyDesc("Canada-Dollar")
                .exchangeRate("1.25")
                .recordDate("2025-09-15")
                .build()
            ))
            .build();
    }

    private ExchangeRateApiResponse getUnitedKingdomPoundExchangeRate() {
        return ExchangeRateApiResponse.builder()
            .data(List.of(
                ExchangeRateData.builder()
                .countryCurrencyDesc("United Kingdom-Pound")
                .exchangeRate("0.73666")
                .recordDate("2025-09-15")
                .build()
            ))
            .build();
    }

}
