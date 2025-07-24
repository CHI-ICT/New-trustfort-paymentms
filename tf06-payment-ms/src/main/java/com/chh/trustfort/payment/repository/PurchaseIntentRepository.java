package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.PurchaseIntent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseIntentRepository extends JpaRepository<PurchaseIntent, Long> {
    Optional<PurchaseIntent> findByTxRef(String txRef);
}
