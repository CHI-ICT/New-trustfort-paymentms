package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.Invoice;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT i FROM Invoice i WHERE i.reference = :reference AND i.amount = :amount AND i.matchKey IS NULL")
    Optional<Invoice> findFirstByReferenceAndAmountAndMatchKeyIsNull(
            @Param("reference") String reference,
            @Param("amount") BigDecimal amount
    );
}
