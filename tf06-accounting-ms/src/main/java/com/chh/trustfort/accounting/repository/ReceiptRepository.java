package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
//    Optional<Receipt> findByPaymentReference(String reference);
    List<Receipt> findByStatusAndDueDateBefore(ReceiptStatus status, LocalDate dueDate);
    List<Receipt> findByMatchKeyIsNull();

    // ReceiptRepository.java
    List<Receipt> findByPaymentReference(String reference);

    List<Receipt> findByPayerEmailAndStatus(String payerEmail, ReceiptStatus status);

    List<Receipt> findByBusinessUnitAndStatus(String businessUnit, ReceiptStatus status);

    List<Receipt> findByPaymentReferenceAndStatus(String reference, ReceiptStatus status);


}