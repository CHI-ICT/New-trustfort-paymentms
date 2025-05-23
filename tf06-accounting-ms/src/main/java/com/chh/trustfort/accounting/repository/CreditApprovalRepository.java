package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.CreditApprovalStatus;
import com.chh.trustfort.accounting.model.CreditApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditApprovalRepository extends JpaRepository<CreditApproval, Long> {

    List<CreditApproval> findByCreditLineId(Long creditLineId);

    List<CreditApproval> findByCreditLineIdAndStatus(Long creditLineId, CreditApprovalStatus status);

    List<CreditApproval> findByApproverIdAndStatus(Long approverId, CreditApprovalStatus status);
}

