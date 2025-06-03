package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.PayableInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PayableInvoiceRepository extends JpaRepository<PayableInvoice, Long> {
    boolean existsByInvoiceHash(String invoiceHash);
    boolean existsByVendorEmailAndDescriptionAndAmount(String vendorEmail, String description, BigDecimal amount);

    List<PayableInvoice> findByStatus(InvoiceStatus status);

    List<PayableInvoice> findByDueDateAndStatusNot(LocalDate dueDate, InvoiceStatus status);

    List<PayableInvoice> findByDueDateBeforeAndStatusNot(LocalDate date, InvoiceStatus status);
    }
