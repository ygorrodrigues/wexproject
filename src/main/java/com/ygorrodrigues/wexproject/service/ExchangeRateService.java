package com.ygorrodrigues.wexproject.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ygorrodrigues.wexproject.exception.CurrencyNotFoundException;
import com.ygorrodrigues.wexproject.models.ExchangeRateApiResponse;
import com.ygorrodrigues.wexproject.models.ExchangeRateData;
import com.ygorrodrigues.wexproject.models.ExchangeRateResponse;
import com.ygorrodrigues.wexproject.models.Purchase;

@Service
public class ExchangeRateService {
    
    private final RestTemplate restTemplate;
    private static final String EXCHANGE_RATE_API_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange";
    
    public ExchangeRateService() {
        this.restTemplate = new RestTemplate();
    }

    public ExchangeRateResponse calculateExchangeRate(String countryCurrency, Purchase purchase) {
        try {
            BigDecimal exchangeRate = getExchangeRate(countryCurrency, purchase.getTransactionDate());
            BigDecimal convertedAmount = purchase.getAmount().multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP); // Round to 2 decimal places (cents)
            
            return new ExchangeRateResponse(
                purchase.getId(),
                purchase.getDescription(),
                purchase.getTransactionDate(),
                purchase.getAmount(),
                "USD",
                convertedAmount,
                countryCurrency,
                exchangeRate
            );
        } catch (CurrencyNotFoundException e) {
            throw new CurrencyNotFoundException("Purchase cannot be converted to the target currency: " + countryCurrency);
        } catch (Exception e) {
            throw new CurrencyNotFoundException("Unable to fetch exchange rate for currency: " + countryCurrency);
        }
    }
    
    public BigDecimal getExchangeRate(String countryCurrency, LocalDate transactionDate) {
        try {
            LocalDate sixMonthsBefore = transactionDate.minusMonths(6);
            
            String url = EXCHANGE_RATE_API_URL + 
                "?fields=country_currency_desc,exchange_rate,record_date" +
                "&filter=country_currency_desc:eq:" + countryCurrency +
                ",record_date:gte:" + sixMonthsBefore.toString() +
                ",record_date:lte:" + transactionDate.toString() +
                "&sort=-record_date" +
                "&page[size]=1" + // Only need the first result, since it is ordered
                "&page[number]=1";
            
            ResponseEntity<ExchangeRateApiResponse> response = restTemplate.getForEntity(url, ExchangeRateApiResponse.class);
            
            return Optional.ofNullable(response.getBody())
                .map(ExchangeRateApiResponse::getData)
                .filter(data -> data != null && !data.isEmpty())
                .map(data -> data.get(0))
                .map(ExchangeRateData::getExchangeRate)
                .map(BigDecimal::new)
                .orElseThrow(() -> new CurrencyNotFoundException("Exchange rate data not found for currency: " + countryCurrency));
        } catch (CurrencyNotFoundException e) {
            // Re-throw currency not found exceptions
            throw e;
        } catch (Exception e) {
            // Log error and throw wrapped exception for other errors
            System.err.println("Error fetching exchange rate: " + e.getMessage());
            throw new CurrencyNotFoundException("Unable to fetch exchange rate for currency: " + countryCurrency, e);
        }
    }
}
