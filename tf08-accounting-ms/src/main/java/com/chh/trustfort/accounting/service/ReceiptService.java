package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.payload.ReceiptGenerationRequest;

public interface ReceiptService {
    Receipt generateReceipt(ReceiptGenerationRequest request);
}