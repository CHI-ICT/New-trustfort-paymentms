package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.BankPaymentRequestDTO;

public interface BankPaymentService {
    boolean initiateTransfer(BankPaymentRequestDTO request);
}
