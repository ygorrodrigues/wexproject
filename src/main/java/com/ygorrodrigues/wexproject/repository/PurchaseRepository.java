package com.ygorrodrigues.wexproject.repository;

import com.ygorrodrigues.wexproject.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    @NonNull
    Optional<Purchase> findById(@NonNull Integer id);
}
