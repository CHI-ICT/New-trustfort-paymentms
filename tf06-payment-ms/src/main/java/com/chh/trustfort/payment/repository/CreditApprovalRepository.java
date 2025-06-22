package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.enums.CreditApprovalStatus;
import com.chh.trustfort.payment.model.facility.CreditApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditApprovalRepository extends JpaRepository<CreditApproval, Long> {

    List<CreditApproval> findByCreditLineId(Long creditLineId);

    List<CreditApproval> findByCreditLineIdAndStatus(Long creditLineId, CreditApprovalStatus status);

    List<CreditApproval> findByApproverIdAndStatus(Long approverId, CreditApprovalStatus status);

    boolean existsByCreditLineIdAndApproverId(Long creditLineId, Long approverId);

}

