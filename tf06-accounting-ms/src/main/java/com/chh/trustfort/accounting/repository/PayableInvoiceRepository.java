package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.PayableInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface PayableInvoiceRepository extends JpaRepository<PayableInvoice, Long> {
    boolean existsByInvoiceHash(String invoiceHash);
    boolean existsByVendorEmailAndDescriptionAndAmount(String vendorEmail, String description, BigDecimal amount);
    }
