// PaymentMovementRepository.java
package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.PaymentMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMovementRepository extends JpaRepository<PaymentMovement, Long> {
}