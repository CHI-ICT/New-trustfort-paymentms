package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentReferenceRepository extends JpaRepository<PaymentReference, Long> {
    Optional<PaymentReference> findByReferenceCode(String referenceCode);
    Optional<PaymentReference> findTopByUserOrderByCreatedAtDesc(Users user);
}
