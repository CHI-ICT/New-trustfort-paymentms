package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.PendingBankTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingBankTransferRepository extends JpaRepository<PendingBankTransfer, Long> {
    Optional<PendingBankTransfer> findByReferenceAndStatus(String reference, String status);
}
