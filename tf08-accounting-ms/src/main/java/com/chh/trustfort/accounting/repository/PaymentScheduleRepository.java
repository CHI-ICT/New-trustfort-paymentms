package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {
    List<PaymentSchedule> findByInvoiceId(Long invoiceId);
}