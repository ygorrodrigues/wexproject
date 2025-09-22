package com.ygorrodrigues.wexproject.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ygorrodrigues.wexproject.exception.CurrencyNotFoundException;
import com.ygorrodrigues.wexproject.handlers.ValidationExceptionHandler;
import com.ygorrodrigues.wexproject.models.ExchangeRateResponse;
import com.ygorrodrigues.wexproject.models.Purchase;
import com.ygorrodrigues.wexproject.models.PurchaseRequest;
import com.ygorrodrigues.wexproject.service.ExchangeRateService;
import com.ygorrodrigues.wexproject.service.PurchaseService;

@ExtendWith(MockitoExtension.class)
public class PurchaseControllerTest {
	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@Mock
	private PurchaseService purchaseService;

	@Mock
	private ExchangeRateService exchangeRateService;

	@InjectMocks
	private PurchaseController purchaseController;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(purchaseController)
				.setControllerAdvice(new ValidationExceptionHandler())
				.build();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	void purchase_ShouldReturnOk_WhenRequestIsValid() throws Exception {
		PurchaseRequest request = PurchaseRequest.builder()
				.description("Coffee")
				.amount(new BigDecimal("12.34"))
				.transactionDate(LocalDate.of(2025, 9, 15))
				.build();

		Purchase saved = Purchase.builder()
				.id(1)
				.description("Coffee")
				.amount(new BigDecimal("12.34"))
				.transactionDate(LocalDate.of(2025, 9, 15))
				.build();

		when(purchaseService.processPurchase(any(PurchaseRequest.class))).thenReturn(saved);

		mockMvc.perform(post("/purchase")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.description").value("Coffee"))
				.andExpect(jsonPath("$.amount").value(12.34))
				.andExpect(jsonPath("$.transactionDate[0]").value(2025))
				.andExpect(jsonPath("$.transactionDate[1]").value(9))
				.andExpect(jsonPath("$.transactionDate[2]").value(15));
	}

	@Test
	void purchase_ShouldReturnBadRequest_WithValidationErrors() throws Exception {
		// Invalid: blank description, negative amount, future date
		PurchaseRequest invalid = PurchaseRequest.builder()
				.description("")
				.amount(new BigDecimal("-10"))
				.transactionDate(LocalDate.now().plusDays(1))
				.build();

		mockMvc.perform(post("/purchase")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalid)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description").value("Description is required"))
				.andExpect(jsonPath("$.amount").value("Amount must be positive"))
				.andExpect(jsonPath("$.transactionDate").value("Transaction date cannot be in the future"));
	}

	@Test
	void getExchangeRate_ShouldReturnOk_WhenPurchaseAndCurrencyAreValid() throws Exception {
		Purchase purchase = Purchase.builder()
				.id(42)
				.description("Laptop")
				.amount(new BigDecimal("1000.00"))
				.transactionDate(LocalDate.of(2025, 9, 15))
				.build();

		ExchangeRateResponse response = ExchangeRateResponse.builder()
				.id(42)
				.description("Laptop")
				.transactionDate(LocalDate.of(2025, 9, 15))
				.originalAmount(new BigDecimal("1000.00"))
				.originalCurrency("USD")
				.convertedAmount(new BigDecimal("1250.00"))
				.targetCurrency("Canada-Dollar")
				.exchangeRate(new BigDecimal("1.25"))
				.build();

		when(purchaseService.findById(eq(42))).thenReturn(purchase);
		when(exchangeRateService.calculateExchangeRate(eq("Canada-Dollar"), eq(purchase))).thenReturn(response);

		mockMvc.perform(get("/purchase/{id}/exchange", 42)
					.param("countryCurrency", "Canada-Dollar"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(42))
				.andExpect(jsonPath("$.description").value("Laptop"))
				.andExpect(jsonPath("$.originalAmount").value(1000.00))
				.andExpect(jsonPath("$.targetCurrency").value("Canada-Dollar"))
				.andExpect(jsonPath("$.exchangeRate").value(1.25));
	}

	@Test
	void getExchangeRate_ShouldReturnNotFound_WhenPurchaseDoesNotExist() throws Exception {
		when(purchaseService.findById(eq(999))).thenReturn(null);

		mockMvc.perform(get("/purchase/{id}/exchange", 999)
					.param("countryCurrency", "Canada-Dollar"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getExchangeRate_ShouldReturnBadRequest_WhenCurrencyIsInvalid() throws Exception {
		Purchase purchase = Purchase.builder()
				.id(7)
				.description("Book")
				.amount(new BigDecimal("20.00"))
				.transactionDate(LocalDate.of(2025, 9, 15))
				.build();

		when(purchaseService.findById(eq(7))).thenReturn(purchase);
		when(exchangeRateService.calculateExchangeRate(eq("Invalid-Currency"), eq(purchase)))
				.thenThrow(new CurrencyNotFoundException("Purchase cannot be converted to the target currency: Invalid-Currency"));

		mockMvc.perform(get("/purchase/{id}/exchange", 7)
					.param("countryCurrency", "Invalid-Currency"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Error: Purchase cannot be converted to the target currency: Invalid-Currency")));
	}
}
