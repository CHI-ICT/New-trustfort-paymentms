package com.chh.trustfort.accounting.repository;


import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByVendorEmailAndCeilingAmount(String vendorEmail, BigDecimal ceilingAmount);
}
