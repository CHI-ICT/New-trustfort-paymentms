package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.BankPaymentRequestDTO;
import com.chh.trustfort.accounting.service.BankPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockBankPaymentServiceImpl implements BankPaymentService {

    @Override
    public boolean initiateTransfer(BankPaymentRequestDTO request) {
        log.info("🔐 Initiating mock bank transfer...");
        log.info("➡ Beneficiary: {}", request.getBeneficiaryName());
        log.info("➡ Account Number: {}", request.getBeneficiaryAccountNumber());
        log.info("➡ Bank Code: {}", request.getBeneficiaryBankCode());
        log.info("➡ Amount: {}", request.getAmount());
        log.info("➡ Narration: {}", request.getNarration());
        log.info("➡ Reference: {}", request.getReference());
        log.info("✅ Transfer simulated successfully.");
        return true;
    }
}
