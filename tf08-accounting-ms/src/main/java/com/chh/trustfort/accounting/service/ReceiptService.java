package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.payload.ReceiptGenerationRequest;

public interface ReceiptService {
    public String generateReceipt(ReceiptGenerationRequest request, AppUser appUser);
}