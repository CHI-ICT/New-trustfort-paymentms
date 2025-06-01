package com.chh.trustfort.accounting.repository;


import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByVendorEmailAndCeilingAmount(String vendorEmail, BigDecimal ceilingAmount);

    boolean existsByContractCode(String contractCode);

    boolean existsByVendorEmailAndCurrencyAndCeilingAmountAndStartDateAndEndDate(
            String vendorEmail, String currency, BigDecimal ceilingAmount, LocalDate startDate, LocalDate endDate);

    List<Contract> findByVendorEmail(String vendorEmail);

}
