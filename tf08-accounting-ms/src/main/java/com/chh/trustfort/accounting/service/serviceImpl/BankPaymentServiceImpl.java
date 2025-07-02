package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.BankPaymentRequestDTO;
import com.chh.trustfort.accounting.service.BankPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankPaymentServiceImpl implements BankPaymentService {

    @Override
    public boolean initiateTransfer(BankPaymentRequestDTO request) {
        log.info("🚀 Simulating bank transfer: {}", request);

        // Simulate mock logic (always succeed in this mock scenario)
        return true;
    }
}
