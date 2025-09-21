package com.ygorrodrigues.wexproject.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WhenAllFieldsAreValid() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "Valid request should not have validation violations");
    }

    @Test
    void shouldFailValidation_WhenDescriptionIsNull() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description(null)
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Description is required", violation.getMessage());
    }

    @Test
    void shouldFailValidation_WhenDescriptionIsBlank() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("   ")
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Description is required", violation.getMessage());
    }

    @Test
    void shouldFailValidation_WhenDescriptionIsEmpty() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("")
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Description is required", violation.getMessage());
    }

    @Test
    void shouldFailValidation_WhenDescriptionExceedsMaxLength() {
        // Given
        String longDescription = "A".repeat(51); // 51 characters, exceeds max of 50
        PurchaseRequest request = PurchaseRequest.builder()
                .description(longDescription)
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Description is too long", violation.getMessage());
    }

    @Test
    void shouldPassValidation_WhenDescriptionIsExactlyMaxLength() {
        // Given
        String maxLengthDescription = "A".repeat(50); // Exactly 50 characters
        PurchaseRequest request = PurchaseRequest.builder()
                .description(maxLengthDescription)
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "Description at max length should be valid");
    }

    @Test
    void shouldFailValidation_WhenAmountIsNull() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(null)
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("amount", violation.getPropertyPath().toString());
        assertEquals("Amount is required", violation.getMessage());
    }

    @Test
    void shouldFailValidation_WhenAmountIsZero() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(BigDecimal.ZERO)
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("amount", violation.getPropertyPath().toString());
        assertEquals("Amount must be positive", violation.getMessage());
    }

    @Test
    void shouldFailValidation_WhenAmountIsNegative() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(new BigDecimal("-100.50"))
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("amount", violation.getPropertyPath().toString());
        assertEquals("Amount must be positive", violation.getMessage());
    }

    @Test
    void shouldPassValidation_WhenAmountIsPositive() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(new BigDecimal("0.01")) // Smallest positive amount
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "Positive amount should be valid");
    }

    @Test
    void shouldFailValidation_WhenTransactionDateIsNull() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(new BigDecimal("100.50"))
                .transactionDate(null)
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("transactionDate", violation.getPropertyPath().toString());
        assertEquals("Transaction date is required", violation.getMessage());
    }

    @Test
    void shouldFailValidation_WhenTransactionDateIsInFuture() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(1);
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(new BigDecimal("100.50"))
                .transactionDate(futureDate)
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<PurchaseRequest> violation = violations.iterator().next();
        assertEquals("transactionDate", violation.getPropertyPath().toString());
        assertEquals("Transaction date cannot be in the future", violation.getMessage());
    }

    @Test
    void shouldPassValidation_WhenTransactionDateIsToday() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "Today's date should be valid");
    }

    @Test
    void shouldPassValidation_WhenTransactionDateIsInPast() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("Valid Purchase")
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now().minusDays(1))
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "Past date should be valid");
    }

    @Test
    void shouldFailValidation_WhenMultipleFieldsAreInvalid() {
        // Given
        PurchaseRequest request = PurchaseRequest.builder()
                .description("") // Invalid: blank
                .amount(new BigDecimal("-50")) // Invalid: negative
                .transactionDate(LocalDate.now().plusDays(1)) // Invalid: future
                .build();

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());
        
        // Verify all expected violations are present
        Set<String> violationProperties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());
        
        assertTrue(violationProperties.contains("description"));
        assertTrue(violationProperties.contains("amount"));
        assertTrue(violationProperties.contains("transactionDate"));
    }

    @Test
    void shouldPassValidation_WhenUsingBuilderWithAllArgsConstructor() {
        // Given
        PurchaseRequest request = new PurchaseRequest(
                "Valid Purchase",
                new BigDecimal("100.50"),
                LocalDate.now()
        );

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "Request created with all-args constructor should be valid");
    }

    @Test
    void shouldPassValidation_WhenUsingNoArgsConstructor() {
        // Given
        PurchaseRequest request = new PurchaseRequest();
        // Note: This will have null values, so it should fail validation
        // This test demonstrates that the no-args constructor creates an object with null fields

        // When
        Set<ConstraintViolation<PurchaseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty(), "Request created with no-args constructor should have validation violations due to null fields");
        assertEquals(3, violations.size(), "All three fields should have validation violations when null");
    }
}
