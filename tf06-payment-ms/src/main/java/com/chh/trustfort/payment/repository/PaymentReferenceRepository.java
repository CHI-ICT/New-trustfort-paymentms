package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentReferenceRepository extends JpaRepository<PaymentReference, Long> {
    Optional<PaymentReference> findByReferenceCode(String referenceCode);
    Optional<PaymentReference> findTopByUserOrderByCreatedAtDesc(Users user);
    List<PaymentReference> findByStatusAndGateway(ReferenceStatus status, String gateway);
    Optional<PaymentReference> findTopByUserAndStatusOrderByCreatedAtDesc(Users user, ReferenceStatus status);

    String referenceCode(String referenceCode);
    Optional<PaymentReference> findByTxRef(String txRef);
    List<PaymentReference> findByStatus(ReferenceStatus status);

}
