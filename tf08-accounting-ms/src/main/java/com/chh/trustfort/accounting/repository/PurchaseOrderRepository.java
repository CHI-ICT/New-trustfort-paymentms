package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNumberAndVendorEmail(String poNumber, String vendorEmail);

    boolean existsByPoNumber(String poNumber);

    boolean existsByVendorEmailAndDescriptionAndAmount(String vendorEmail, String description, BigDecimal amount);

    Optional<PurchaseOrder> findByVendorEmailAndDescriptionAndAmount(String vendorEmail, String description, BigDecimal amount);

}