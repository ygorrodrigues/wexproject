package com.ygorrodrigues.wexproject.repository;

import com.ygorrodrigues.wexproject.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    Optional<Purchase> findById(UUID id);
}
