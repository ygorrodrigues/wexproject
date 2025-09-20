package com.ygorrodrigues.wexproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
public class Purchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @NotNull(message = "Transaction date is required")
    @Column(nullable = false)
    private LocalDate transactionDate;
    
    public Purchase(String description, BigDecimal amount, LocalDate transactionDate) {
        this.description = description;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }
    
}
