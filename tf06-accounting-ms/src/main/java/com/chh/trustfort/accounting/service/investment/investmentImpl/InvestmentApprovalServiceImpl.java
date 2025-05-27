package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.investment.InvestmentApprovalRequestDTO;
import com.chh.trustfort.accounting.enums.InvestmentVoucherStatus;
import com.chh.trustfort.accounting.model.InvestmentVoucher;
import com.chh.trustfort.accounting.model.InvestmentVoucherApprovalLog;
import com.chh.trustfort.accounting.repository.InvestmentVoucherApprovalLogRepository;
import com.chh.trustfort.accounting.repository.InvestmentVoucherRepository;
import com.chh.trustfort.accounting.service.investment.InvestmentApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InvestmentApprovalServiceImpl implements InvestmentApprovalService {

    @Autowired
    private InvestmentVoucherRepository voucherRepository;

    @Autowired
    private InvestmentVoucherApprovalLogRepository approvalLogRepository;

    @Override
    public void submitForApproval(Long voucherId) {
        InvestmentVoucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));

        voucher.setStatus(InvestmentVoucherStatus.PENDING);
        voucher.setCreatedAt(LocalDateTime.now());
        voucherRepository.save(voucher);

        System.out.println("Voucher submitted for approval: " + voucherId);
    }

    @Override
    public void approve(InvestmentApprovalRequestDTO dto) {
        InvestmentVoucher voucher = voucherRepository.findById(dto.voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));

        voucher.setStatus(InvestmentVoucherStatus.APPROVED);
        voucherRepository.save(voucher);

        InvestmentVoucherApprovalLog log = new InvestmentVoucherApprovalLog();
        log.setVoucherId(dto.voucherId);
        log.setApprover(dto.approver);
        log.setApproved(true);
        log.setComment(dto.comment);
        log.setDecisionTime(LocalDateTime.now());
        approvalLogRepository.save(log);

        System.out.println("Voucher " + dto.voucherId + " approved by " + dto.approver);
    }
}
