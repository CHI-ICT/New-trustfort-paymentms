// 2. REPOSITORY: PaymentInstallmentRepository.java
package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.PaymentInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentInstallmentRepository extends JpaRepository<PaymentInstallment, Long> {
    List<PaymentInstallment> findByInvoiceId(Long invoiceId);

    boolean existsByInvoiceId(Long invoiceId);

}